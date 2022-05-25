import React, { Component } from 'react';
import { Button, Card, Col, Container, Row } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import Emoji from './Emoji';
import ModalAvailabilityEditor from './edit/ModalAvailabilityEditor';
import axios from 'axios';
import ReactTable from 'react-table';
import StrengthRadar from './StrengthRadar';
import 'react-table/react-table.css';
import { API_ROOT } from '../data/apiConfig';

class BeersList extends Component {

  constructor(props: any) {
    super(props);
    this.state = {
      items: [],
      title: "",
      description: "",
      searchString: null,
      isLoading: false,
      expandedView: false,
      isDisplayingAdditionalStuff: false,
    };

    this.toggleIsDisplayingAdditionalStuff = this.toggleIsDisplayingAdditionalStuff.bind(this);
    this.toggleExpandedView = this.toggleExpandedView.bind(this);
    this.showModal = this.showModal.bind(this);
    this.componentDidMount = this.componentDidMount.bind(this);
    this.temp = this.temp.bind(this);
  }

  toggleIsDisplayingAdditionalStuff() {
    this.setState(state => ({
      isDisplayingAdditionalStuff: !state.isDisplayingAdditionalStuff
    }));
  }

  toggleExpandedView() {
    this.setState(state => ({
      expandedView: !state.expandedView
    }));
  }

  temp() {
    let postBottleAvailabilityUrl = `${API_ROOT}/public/beers/test`;

    var self = this;
    axios.put(
      postBottleAvailabilityUrl,
      'Woot',
      { withCredentials: true,
        headers: {"Content-Type": "text/plain"}})
    .then(function (response){
       self.fetchData(self.props.listName, self.props.listId);
     }).catch(function (error) {
       console.log(error);
       alert("Ebriété assumée, erreur assurée bis");
     }
    );
  }

  componentDidMount() {
    if (this.props.searchString)
      this.search(this.props.searchString);
    else
      this.fetchData(this.props.listName, this.props.listId);
  }

  componentWillReceiveProps(nextProps) {
    let oldListId = this.props.listId;
    let newListId = nextProps.listId;
    let oldListName = this.props.listName;
    let newListName = nextProps.listName;
    let oldSearchString = this.props.searchString;
    let newSearchString = nextProps.searchString;

    if (newSearchString) {
      if (oldSearchString !== newSearchString) {
        this.search(newSearchString);
      }
    } else if (oldListId !== newListId || oldListName !== newListName) {
         this.fetchData(newListName, newListId);
    }
  }

  showModal() {
    this.setState({showModal: true});
  }

  fetchData = async (listName, listId) => {
    this.setState({isLoading: true});

    let listUrl = `${API_ROOT}` + (this.props.isAuthenticated ? '/private' : '/public');
    listUrl += '/beers/';
    if (listName && listId)
      listUrl += listName + '/' + listId;

    fetch(listUrl, {credentials: 'include'})
      .then(response => response.json())
      .then(list =>
        this.setState({
          items: list.beers,
          title: list.name,
          description: list.description,
          isLoading: false
        })
      );
  }

  search = async (searchString) => {
    this.setState({isLoading: true});

    axios.get(
      `${API_ROOT}/public/beers/search`,
      {
        params: {
          searchedText: searchString
        },
        withCredentials: true,
        headers: {"Content-Type": "text/plain"}
      })
    .then(response => response.data)
    .then(list =>
      this.setState({
        items: list.beers,
        title: 'Résultat de la recherche',
        description: 'Boissons contenant quelque part "'+searchString+'"',
        isLoading: false
      })
    );
  }

  render() {
    const {
      items,
      title,
      description,
      isLoading,
      expandedView,
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }

    const isAuthenticated = this.props.isAuthenticated;
    const hasTap = items.some(beer => beer.tapPriceSmall && beer.tapPriceBig);
    const hasBottle = items.some(beer => beer.bottleSellingPrice && beer.bottleVolumeInCl);

    //TODO Better tooltip formating with bar names or multiline prices + tooltip for bars
    return (
      <Container style={{padding: 0 }}>
        <h2>{title}</h2>
        <p>{description}</p>
        {this.props.isAuthenticated && false &&
          <Button className="float-right" onClick={this.toggleIsDisplayingAdditionalStuff}>
            {this.state.isDisplayingAdditionalStuff
              ? <Emoji symbol="➖" label="Masquer colonnes supplementaires"/>
              : <Emoji symbol="➕" label="Afficher colonnes supplementaires"/>
            }
          </Button>
        }

        <ReactTable
          className="-striped -highlight"
          defaultPageSize={25}
          defaultSorteded={'name'}
          data={items}
          columns={[
            {
              Header: 'Bière',
              columns: [
              {
                Header: 'Nom',
                accessor: 'name',
                Cell: row => <Link to={'/beerid/'+row.original.id}>{row.original.name}</Link>,
                minWidth: 150,
                sortable: true,
              },{
                Header: 'Alc.',
                accessor: 'abv',
                Cell: row => <div align="right">{this.formatAlc(row.value)}</div>,
                minWidth: 60,
                sortable: true,
              },{
                Header: 'Type',
                accessor: 'styleName',
                Cell: row => <Link to={'/list/styles/'+row.original.styleId}>{row.original.styleName}</Link>,
                minWidth: 80,
                sortable: true,
              },{
                Header: 'Couleur',
                accessor: 'colorName',
                Cell: row => <Link to={'/list/colors/'+row.original.colorId}>{row.original.colorName}</Link>,
                show: expandedView,
                sortable: true,
              }]
            },{
              Header: 'Brasserie',
              columns: [
              {
                Header: 'Nom',
                accessor: 'producerName',
                Cell: row => <Link to={'/list/producers/'+row.original.producerId}>{row.original.producerName}</Link>,
                show: expandedView,
                sortable: true,
              },{
                Header: 'Origine',
                accessor: 'producerOriginShortName',
                Cell: row => <Link to={'/list/origins/'+row.original.producerOriginId}>{row.original.producerOriginShortName}</Link>,
                show: expandedView,
                sortable: true,
              }]
            },{
              Header: 'Bouteille',
              columns: [
              {
                Header: 'Vol.',
                accessor: 'bottleVolumeInCl',
                Cell: row => row.value ? row.value+' cl' : '',
                sortable: true,
                minWidth: 40,
                show: hasBottle && expandedView,
              },{
                Header: 'Prix',
                accessor: 'bottleSellingPrice',
                Cell: row => this.formatPrice(row.value),
                sortable: true,
                minWidth: 30,
                show: hasBottle && expandedView,
              },{
                Header: 'Dispo.',
                accessor: 'bottleAvailability',
                Cell: row => this.formatAvailability(row.value, false),
                sortable: true,
                minWidth: 20,
                show: hasBottle && expandedView,
              }]
            },{
              Header: 'Pression',
              columns: [
              {
                Header: 'Dispo.',
                accessor: 'tapAvailability',
                Cell: row => this.formatAvailability(row.value, false),
                sortable: true,
                minWidth: 20,
                show: hasTap && expandedView,
              },{
                Header: '25cl',
                accessor: 'tapPriceSmall',
                Cell: row => this.formatPrice(row.value),
                sortable: true,
                minWidth: 25,
                show: hasTap && expandedView,
              },{
                Header: '50cl',
                accessor: 'tapPriceBig',
                Cell: row => this.formatPrice(row.value),
                sortable: true,
                minWidth: 25,
                show: hasTap && expandedView,
              }]
            },{
              id: 'price',
              Header: 'Prix',
              accessor: d => d.tapPriceSmall ? d.tapPriceSmall : d.bottleSellingPrice,
              Cell: row => this.formatBeerPrices(row.original),
              minWidth: 120,
            },{
              id: 'dispo',
              Header: 'Dispo',
              accessor: d => d.tapAvailability ? d.tapAvailability : d.bottleAvailability,
              Cell: row => this.formatBeerAvailability(row.original),
              sortable: true,
              minWidth: 45,
            },{
              Header: 'God',
              Cell: row => <Emoji symbol="✏" label="Edition" onClick={() => this.setState({beerToUpdate: row.original})}/>,
              minWidth: 44,
              show: isAuthenticated === true,
            }
          ]}
          SubComponent={row => {
                    return (
                      <Row>
                        <div className="col-sm-6 py-1" style={{padding: 0 }}>
                          <Card className="h-100">
                            <Card.Body>
                              <Row>
                                <Col xs={4}><b>Brasseur</b></Col>
                                <Col><Link to={'/list/producers/'+row.original.producerId}>{row.original.producerName}</Link> (<Link to={'/list/origins/'+row.original.producerOriginId}>{row.original.producerOriginName}</Link>)</Col>
                              </Row><Row>
                                <Col xs={4}><b>Couleur</b></Col>
                                <Col><Link to={'/list/colors/'+row.original.colorId}>{row.original.colorName}</Link></Col>
                              </Row>
                              {row.original.bottleVolumeInCl && (
                                <Row>
                                  <Col xs={4}><b>Volume</b></Col>
                                  <Col>{row.original.bottleVolumeInCl+' cl'}</Col>
                                </Row>)}
                              {row.original.tapAvailability && (
                                <Row>
                                  <Col xs={4}><b>Pression</b></Col>
                                  <Col>{this.formatAvailability(row.original.tapAvailability, true)}</Col>
                                </Row>)}
                              {row.original.bottleAvailability && (
                                <Row>
                                  <Col xs={4}><b>Bouteille</b></Col>
                                  <Col>{this.formatAvailability(row.original.bottleAvailability, true)}</Col>
                                </Row>)}
                              {(row.original.bitternessRank || row.original.hoppingRank || row.original.sweetnessRank || row.original.sournessRank) &&
                                (<div align="center">
                                  <hr/>
                                  <StrengthRadar
                                    bitterness={row.original.bitternessRank}
                                    hopping={row.original.hoppingRank}
                                    sweetness={row.original.sweetnessRank}
                                    sourness={row.original.sournessRank}/>
                                </div>)}
                            </Card.Body>
                          </Card>
                        </div>
                        <div className="col-sm-6 py-1" style={{padding: 0 }}>
                          <Card className="h-100">
                            <Card.Body>
                              <p>{row.original.comment}</p>
                            </Card.Body>
                          </Card>
                        </div>
                      </Row>
                    );
                  }}
        />

        <ModalAvailabilityEditor beerToUpdate={this.state.beerToUpdate} onClose={this.componentDidMount}/>
      </Container>
    );

  }

  formatBeerPrices(beer) {
    if (beer.tapAvailability)
      return (<div align="right">{this.formatTapPrices(beer.tapPriceSmall, beer.tapPriceBig)}</div>)
    else
      return (<div align="right">{this.formatPrice(beer.bottleSellingPrice)}</div>);
  }

  formatBeerAvailability(beer) {
    if (beer.tapAvailability)
      return (<div align="center">{this.formatAvailability(beer.tapAvailability, false)}</div>)
    else
      return (<div align="center">{this.formatAvailability(beer.bottleAvailability, false)}</div>);
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

  formatAlc(abv) {
      if (!abv)
        return null;
      else
        return abv.toFixed(1) + "%";
  }

  translateAvailability(availability, emoji) {
    switch (availability) {
      case "NOT_YET_AVAILABLE":
        return emoji ? '🕑' : 'Prochainement';
      case "NEARLY_OUT_OF_STOCK":
      case "AVAILABLE":
        return emoji ? '✅' : 'Disponible';
      case "OUT_OF_STOCK":
        return emoji ? '❌' : 'Epuisée';
      default:
        return null;
    }
  }

  formatAvailability(availability, withLabel) {
    let label = this.translateAvailability(availability, false);
    let logo = this.translateAvailability(availability, true);

    if (withLabel)
      return <Emoji symbol={logo} label={label} text={label}/>
    else
      return <Emoji symbol={logo} label={label}/>;
  }

  formatBottlePriceCalculation(buyingPrice, volumeInCl, sellingPrice, abv) {
    return (
      <td>
        Achat : {this.formatPrice(buyingPrice)} {volumeInCl && '('+volumeInCl+' cL)'} (Min vente : {this.displayBottleMinPrice(buyingPrice)})<br/>
        Vente : {this.formatPrice(sellingPrice)} (prix 1cl alc. : {this.displayAlcoolPrice(sellingPrice, volumeInCl, abv)})
      </td>
    );
  }

  formatTapPriceCalculation(buyingPrice, volumeInCl, sellingPrice, abv) {
    return (
      <td>
        Achat : {this.formatPrice(buyingPrice*volumeInCl/100)} (Min vente : {this.displayTapMinPrice(buyingPrice, volumeInCl)})<br/>
        Vente : {this.formatPrice(sellingPrice)} (prix 1cl alc. : {this.displayAlcoolPrice(sellingPrice, volumeInCl, abv)})
      </td>
    );
  }

  displayTapMinPrice(buyingPrice, volumeInCl) {
    return this.formatPrice(buyingPrice*volumeInCl*3/100);
  }

  displayBottleMinPrice(buyingPrice) {
    return this.formatPrice(buyingPrice*2.3);
  }

  displayAlcoolPrice(sellingPrice, volumeInCl, abv) {
    if (!sellingPrice || !volumeInCl || !abv)
      return null;
    else
      return this.formatPrice((sellingPrice*100) / (volumeInCl*abv));
  }
}

export default BeersList;
