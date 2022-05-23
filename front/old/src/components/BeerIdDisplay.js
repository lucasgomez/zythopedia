import { Component } from 'react';
import { Card, Col, Container, Row } from 'react-bootstrap';
import StrengthRadar from './StrengthRadar';
import Emoji from './Emoji';

class BeerIdDisplay extends Component {

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

  render() {
    const {
      beer,
      isLoading
    } = this.props;

    if (isLoading || !beer) {
      return <div className="container"><p> Loading... < /p></div>;
    }

    let cardClassName = "col-sm-6 col-md-6 col-lg-6 py-1";
    let barCardClassName = "col-sm-6 col-md-6 col-lg-6 py-1";
    return (
      <Container>
        <Row>
          <div className={cardClassName}>
            <Card className="h-100" bg="light">
              <Card.Body style={{padding: '2.5rem'}}>
                <h2>{beer.name}</h2>
                <h4>{beer.producerName}{' - '}{beer.producerOriginName}</h4>
              </Card.Body>
            </Card>
          </div>

          {beer.bottleBars && beer.bottleBars.map((bar: any) =>
            <BarDisplay bar={bar} beer={beer} type="bottle" cardClassName={barCardClassName}/>
          )}
          {beer.tapBars && beer.tapBars.map((bar: any) =>
            <BarDisplay bar={bar} beer={beer} type="tap" cardClassName={barCardClassName}/>
          )}

          <div className={cardClassName}>
            <Card className="h-100" bg="dark" text="light">
              {beer.comment &&
                 (<Card.Body style={{padding: '2.5rem'}}>
                   <Card.Title><b>Description</b></Card.Title>
                    <h5>{beer.comment}</h5>
                  </Card.Body>)}
            </Card>
          </div>
          <div className={cardClassName}>
            <Card className="h-100" bg="light">
              <Card.Body>
                <NamedLabel name="Alcool" value={beer.abv+' %'}/>
                <NamedLabel name="Couleur" value={beer.colorName}/>
                <NamedLabel name="Style" value={beer.styleName}/>
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
        </Row>
      </Container>
    );

  }
}

const BarDisplay = (props) => {
  return (
    <div className={props.cardClassName}>
      <Card className="h-100" bg="dark" text="light">
        <Card.Body style={{padding: '2.5rem', color: "#f8f9fa"}}>
          <Card.Title>{props.bar.name}</Card.Title>
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
      <Table striped style={{color: "#f8f9fa"}}>
        <Row>
          <Col xs={6}><h5>{props.beer.bottleVolumeInCl+"cl"}</h5></Col>
          <Col xs={6}><h5>{props.beer.bottleSellingPrice+".-"}</h5></Col>
        </Row>
      </Table>);
  if (props.type === "tap")
    return (
      <Table striped style={{color: "#f8f9fa"}}>
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

export default BeerIdDisplay;
