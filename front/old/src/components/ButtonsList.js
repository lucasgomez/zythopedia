import React, { Component } from 'react';
import { NavDropdown } from 'react-bootstrap';
import { API_ROOT } from '../data/apiConfig';

class ButtonsList extends Component {
  constructor(props: any) {
    super(props);

    this.state = {
      items: [],
      isLoading: false
    };
  }

  componentDidMount() {
    this.setState({
      isLoading: true
    });
    let baseUrl = `${API_ROOT}/public/lists/` + this.props.listName;

    fetch(baseUrl)
      .then(response => response.json())
      .then(items =>
        this.setState({
          items: items,
          isLoading: false
        })
      );
  }

  render() {
    const {
      items,
      isLoading
    } = this.state;
    const listName = this.props.listName;
    const title = this.props.title;

    if (isLoading) {
      return (
        <NavDropdown title={title} id={'buttonsList-'+title}>
          <NavDropdown.Item>
            Loading...
          </NavDropdown.Item>
        </NavDropdown>
      );
    }

    return (
      <NavDropdown title={title} id={'nav-dropdown-'+title}>
        {items.map((item: any) =>
          <NavDropdown.Item key={'dropdown-'+title+'-item'+item.id} tag="a" href={'/list/'+listName+'/'+item.id}>
            {item.name}
          </NavDropdown.Item>
        )}
      </NavDropdown>
    );
  }
}

export default ButtonsList;
