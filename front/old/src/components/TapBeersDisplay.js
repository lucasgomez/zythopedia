import React, { Component } from 'react';
import { Badge, Card, Col, Container, Row } from 'react-bootstrap';
import Emoji from './Emoji';
import { API_ROOT } from '../data/apiConfig';


class TapBeersDisplay extends Component {

  constructor(props) {
    super(props);

    this.state = {
      items: [],
      title: "",
      description: "",
      updateTime: null,
      isLoading: false
    };
  }

  componentDidMount() {
    this.fetchData(this.props.listName, this.props.listId);

    this.interval = setInterval(this.tick, 1000*60*2);
  }

  componentWillUnmount() {
     clearInterval(this.interval);
  }

  tick = () => {
    this.fetchData(this.state.listName, this.state.listId);
  }

  componentWillReceiveProps(nextProps) {
    let oldListId = this.props.listId;
    let newListId = nextProps.listId;
    let oldListName = this.props.listName;
    let newListName = nextProps.listName;
    if (oldListId !== newListId || oldListName !== newListName) {
         this.fetchData(newListName, newListId);
    }
  }

  fetchData = async (listName, listId) => {
    this.setState({
      isLoading: true
    });

    let listUrl = `${API_ROOT}/public/beers/`;
    if (listName && listId)
      listUrl += listName + '/' + listId;

    fetch(listUrl)
      .then(response => response.json())
      .then(list =>
        this.setState({
          items: list.beers.filter(beer => this.isAvailable(beer))
            .sort((a, b) => {
              if (a.styleName === b.styleName){
                return a.name < b.name ? -1 : 1;
              }
              return a.styleName < b.styleName ? -1 : 1;
            }),
          title: list.name,
          listName: listName,
          listId: listId,
          updateTime: new Date(),
          isLoading: false
        })
      );
  }

  isAvailable(beer) {
    if (beer.tapAvailability)
      return !['NOT_YET_AVAILABLE', 'OUT_OF_STOCK'].includes(beer.tapAvailability)
    else
      return !['NOT_YET_AVAILABLE', 'OUT_OF_STOCK'].includes(beer.bottleAvailability);
  }

  renderProducer = (beer) => {
    let producerString = beer.producerName
    if (beer.producerOriginShortName)
      producerString += " ("+beer.producerOriginShortName+")"

    return producerString
  }

  getAvailibityColor = (beer) => {
    let deltaInMinutes = null;

    if (beer.tapAvailabilityDate)
      deltaInMinutes = (new Date() - new Date(beer.tapAvailabilityDate)) / (60*1000);

    if (beer.tapAssortment === "FIXED")
      return "light"
    else if (beer.tapAvailability === "NEARLY_OUT_OF_STOCK")
      return "danger"
    else if (deltaInMinutes && deltaInMinutes < 60)
      return "success"
    else
      return "warning";
  }

  getTextColor = (bgColor) => {
    let lightColors = ["warning", "danger", "light", "success"];
    return lightColors.includes(bgColor)
      ? "dark"
      : "light";
  }

  createBeerCard = (beer, isPair) => {
    let bgColor = null;

    if (beer.tapAvailability)
      bgColor = this.getAvailibityColor(beer)
    else
      bgColor = isPair ? "dark" : "light";

    let textColor= this.getTextColor(bgColor);

    return (
      <Col>
        <Card className="h-100" bg={bgColor} text={textColor} border="light" >
            {beer.tapAvailability && (
              <Card.Body style={{padding: '0.5rem'}}>
                <h3 class="card-title"><b>{beer.name}</b><div class="float-right"><h4><b>{this.formatBeerPrices(beer)}</b></h4></div></h3>
                <h7 class="card-subtitle mb-2">{this.renderProducer(beer)}</h7>
                <div><b>{beer.abv}% {beer.bottleVolumeInCl} {beer.bottleVolumeInCl && "cl "}{beer.styleName} ({beer.colorName})</b></div>
              </Card.Body>
            )}
            {beer.bottleAvailability && (
              <Card.Body style={{padding: '0.5rem'}}>
                <h1 class="card-title">{beer.name}<div class="float-right"><h2>{this.formatBeerPrices(beer)}</h2></div></h1>
                <div><b>{beer.abv}% {beer.bottleVolumeInCl} {beer.bottleVolumeInCl && "cl "}{beer.styleName} ({beer.colorName})</b></div>
              </Card.Body>
            )}
        </Card>
      </Col>
    );
  }

  formatBeerPrices(beer) {
    if (beer.tapAvailability)
      return (<div align="right">{this.formatTapPrices(beer.tapPriceSmall, beer.tapPriceBig)}</div>)
    else
      return (<div align="right">{this.formatPrice(beer.bottleSellingPrice)}</div>);
  }

  formatPrice(price) {
      if (!price)
        return null;
      else
        return price.toFixed(2) + ".-";
  }

  formatTapPrices(priceSmall, priceBig) {
      if (!priceSmall && !priceBig)
        return null;
      else
        return priceSmall.toFixed(2) + ".- / " +  priceBig.toFixed(2) + ".-";
  }

  createTable = (items, rowLength) => {
    let table = []

    for (let rowId = 0; rowId < (items.length/rowLength); rowId++) {
      let children = []

      for (let colId = 0; colId < rowLength; colId++) {
        let isEven = (colId+rowId)%2 === 0
        let cellId = (rowId*rowLength)+colId

        if (cellId<items.length)
          children.push(this.createBeerCard(items[cellId], isEven))
        else
          children.push(<Col/>)
      }
      table.push(<Row noGutters="true">{children}</Row>)
    }
    return table
  }

  render() {
    const {
      items,
      isLoading,
      updateTime
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }
    let isTap = items.some(beer => beer.tapAvailability);
    let numberOfColumns = isTap ? 3 : 4;
    let title = isTap ? "Pressions - Liste de prix (25 / 50 cL)" : "Bouteilles";

    return (
      <Container fluid="true">
        <Row>
          <h1 class="col text-center"><Emoji symbol="🍺" label="Choppe"/> {title} <Emoji symbol="🍻" label="Choppes faisant santé"/></h1>
        </Row>

        {this.createTable(items, numberOfColumns)}

        <p>{updateTime && 'MàJ : '+('0'+updateTime.getHours()).slice(-2)+":"+('0'+updateTime.getMinutes()).slice(-2)}</p>
        {isTap &&
      		(<Row className="text-center">
      			<h5 class="col"><Badge pill variant="light">Fixe</Badge></h5>
      			<h5 class="col"><Badge pill variant="success">Nouvelle</Badge></h5>
      			<h5 class="col"><Badge pill variant="warning">Temporaire</Badge></h5>
      			<h5 class="col"><Badge pill variant="danger">Bientôt épuisée</Badge></h5>
      		</Row>)}
      </Container>
    );
  }
}

export default TapBeersDisplay;
