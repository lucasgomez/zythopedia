import { Component } from 'react';
import { Card, Container, Row } from 'react-bootstrap';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import { API_ROOT } from '../data/apiConfig';

class BottleBarDisplay extends Component {

  constructor(props: any) {
    super(props);
    this.state = {
      items: [],
      isLoading: false,
    };
  }

  componentDidMount() {
    this.fetchData();
  }

  fetchData = async () => {
    this.setState({isLoading: true});
    fetch(
      `${API_ROOT}/public/beers/bars/667`,
      {credentials: 'include'})
      .then(response => response.json())
      .then(list =>
        this.setState({
          items: list.beers,
          isLoading: false
        })
      );
  }


  render() {
    const {
      items,
      isLoading,
    } = this.state;

    if (isLoading) {
      return <p > Loading... < /p>;
    }

    let cardClassName = "col-sm-6 col-md-6 col-lg-6 py-1";
    let columns = [
        {
          Header: 'Nom',
          accessor: 'name',
          minWidth: 170,
          sortable: true,
        },{
          Header: 'Alc.',
          accessor: 'abv',
          Cell: row => <div align="right">{this.formatAlc(row.value)}</div>,
          minWidth: 47,
          sortable: true,
        },{
          Header: 'Type',
          accessor: 'styleName',
          minWidth: 105,
          sortable: true,
        },{
          Header: 'Couleur',
          accessor: 'colorName',
          minWidth: 83,
          sortable: true,
        },{
          Header: 'Brasserie',
          accessor: 'producerName',
          Cell: row => this.formatProducer(row.original),
          minWidth: 225,
          sortable: true,
        },{
          Header: 'Vol.',
          accessor: 'bottleVolumeInCl',
          Cell: row => row.value ? row.value+' cl' : '',
          sortable: true,
          minWidth: 60,
        },{
          Header: 'Prix',
          accessor: 'bottleSellingPrice',
          Cell: row => this.formatPrice(row.value),
          sortable: true,
          minWidth: 65,
        }
      ];

    //TODO Better tooltip formating with bar names or multiline prices + tooltip for bars
    return (
      <Container fluid style={{padding: 0 }}>
        <Row>
          <div className={cardClassName}>
            <Card className="h-100" bg="light">
              <Card.Body style={{padding: 0}}>
                <ReactTable
                  data={items}
                  columns={columns}
                  page={0}
                  defaultPageSize={20}
                  className="-striped -highlight"
                  defaultSorteded={'styleName'}
                  showPagination={false}/>
              </Card.Body>
            </Card>
          </div>
          <div className={cardClassName}>
            <Card className="h-100" bg="light">
              <Card.Body style={{padding: 0}}>
                <ReactTable
                  data={items}
                  columns={columns}
                  page={1}
                  defaultPageSize={20}
                  className="-striped -highlight"
                  defaultSorteded={'styleName'}
                  showPagination={false}/>
              </Card.Body>
            </Card>
          </div>
        </Row>
      </Container>
    );

  }

  formatProducer(beer) {
      if (!beer.producerName)
        return <div/>;
      else
        return beer.producerName + " (" + beer.producerOriginShortName + ")";
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

  formatAlc(abv) {
      if (!abv)
        return null;
      else
        return abv.toFixed(1) + "%";
  }

}

export default BottleBarDisplay;
