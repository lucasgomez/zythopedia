import React, { Component } from 'react';
import { Card, Col, Container, Row, Table } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import StrengthRadar from './StrengthRadar';
import Emoji from './Emoji';
import { API_ROOT } from '../data/apiConfig';

class BeerId extends Component {
  constructor(props) {
    super(props);

    this.state = {
      beer: null,
      isLoading: false,
      isAuthenticated: this.props.isAuthenticated,
    };
  }

  componentDidMount() {
    this.fetchData(this.props.beerId);
  }

  componentWillReceiveProps(nextProps) {
    let newBeerId = nextProps.beerId;
    let oldBeerId = this.props.beerId;
    if(newBeerId !== oldBeerId) {
       this.fetchData(newBeerId);
    }
  }


  translateAvailability(availability, emoji) {
    switch (availability) {
      case "NOT_YET_AVAILABLE":
        return emoji ? '🕑' : 'Prochainement disponible';
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

  fetchData = (beerId) => {
    this.setState({
      isLoading: true
    });

    let beerUrl = `${API_ROOT}/public/beers/` + beerId;
    fetch(beerUrl)
      .then(response => response.json())
      .then(item =>
        this.setState({
          beer: item,
          isLoading: false
        })
      );
  }

  render() {
    const {
      beer,
      isLoading
    } = this.state;

    if (isLoading || !beer) {
      return <div className="container"><p> Loading... < /p></div>;
    }

    let cardClassName = "col-sm-6 col-md-6 col-lg-4 py-1";
    let barCardClassName = "col-sm-12 col-md-12 col-lg-4 py-1";
    return (
      <Container style={{padding: 0}}>
        <Row>
          <div className="col-sm-12">
            <Card>
              <Card.Body>
                <h2>{beer.name}</h2>
                <h4>
                <Link to={'/list/producers/'+beer.producerId}>{beer.producerName}</Link>
                {' - '}
                <Link to={'/list/origins/'+beer.producerOriginId}>{beer.producerOriginName}</Link>
                </h4>
              </Card.Body>
            </Card>
          </div>

          <Col xs={6} md={6}>
            {this.props.isAuthenticated
              ? <Link className="float-right" to={'/edit/beer/'+beer.id}>✏</Link>
              : <div/>
            }
          </Col>
        </Row>

        <Row>
          <div className={cardClassName}>
            <Card className="h-100">
              <Card.Body>
                <Card.Title>Description</Card.Title>
                <p>{beer.comment}</p>
              </Card.Body>
            </Card>
          </div>
          <div className={cardClassName}>
            <Card className="h-100">
              <Card.Body>
                <Card.Title>Détails</Card.Title>
                <NamedLabel name="Alcool" value={beer.abv+' %'}/>
                <NamedLabel name="Couleur" value={<Link to={'/list/colors/'+beer.colorId}>{beer.colorName}</Link>}/>
                <NamedLabel name="Style" value={<Link to={'/list/styles/'+beer.styleId}>{beer.styleName}</Link>}/>
                <NamedLabel name="Fermentation" value={beer.fermenting}/>
                <div align="center">
                  <StrengthRadar
                    bitterness={beer.bitternessRank}
                    hopping={beer.hoppingRank}
                    sweetness={beer.sweetnessRank}
                    sourness={beer.sournessRank}/>
                </div>
              </Card.Body>
            </Card>
          </div>

          {beer.bottleBars && beer.bottleBars.map((bar) =>
            <BarDisplay bar={bar} beer={beer} type="bottle" cardClassName={barCardClassName}/>
          )}
          {beer.tapBars && beer.tapBars.map((bar) =>
            <BarDisplay bar={bar} beer={beer} type="tap" cardClassName={barCardClassName}/>
          )}
        </Row>
      </Container>
    );

  }
}

const BarDisplay = (props) => {
  return (
    <div className={props.cardClassName}>
      <Card className="h-100">
        <Card.Body>
          <Card.Title><Link to={'/list/bars/'+props.bar.id}>{props.bar.name}</Link></Card.Title>
          <p>{props.bar.comment}</p>
          <PriceDisplay beer={props.beer} type={props.type}/>
        </Card.Body>
      </Card>
    </div>
  );
}

const PriceDisplay = (props) => {
  if (props.type === "bottle")
    return (
      <Table striped>
        <Row>
          <Col xs={6}><h5>{props.beer.bottleVolumeInCl+"cl"}</h5></Col>
          <Col xs={6}><h5>{props.beer.bottleSellingPrice+".-"}</h5></Col>
        </Row>
      </Table>);
  if (props.type === "tap")
    return (
      <Table striped>
        <Row>
          <Col xs={6}><h5>{"25cl"}</h5></Col>
          <Col xs={6}><h5>{props.beer.tapPriceSmall+".-"}</h5></Col>
        </Row>
        <Row>
          <Col xs={6}><h5>{"50cl"}</h5></Col>
          <Col xs={6}><h5>{props.beer.tapPriceBig+".-"}</h5></Col>
        </Row>
      </Table>);
  return <div/>;
}

const NamedLabel = (props) => (
  <div>
    {
      props.value ?
        <Row>
          <Col md={6} xs={6}><b>{props.name}</b></Col>
          <Col md={6} xs={6}>{props.value}</Col>
        </Row>
      :
        <Row>
          <Col md={6} xs={6}></Col>
          <Col md={6} xs={6}></Col>
        </Row>
    }
  </div>
);

export default BeerId;
