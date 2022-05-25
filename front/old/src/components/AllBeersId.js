import React, { Component } from 'react';
import { Container } from 'react-bootstrap';
import BeerIdDisplay from './BeerIdDisplay';
import { API_ROOT } from '../data/apiConfig';

class AllBeersId extends Component {

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
      `${API_ROOT}/public/beers/bars/666`,
      {credentials: 'include'}
    )
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

    return (
      <Container>
        {items.map(beer =>
          <div style={{'page-break-inside': 'avoid;'}}>
            <BeerIdDisplay beer={beer} isLoading={isLoading}/>
          </div>
        )}
      </Container>
    );
  }
}

export default AllBeersId;
