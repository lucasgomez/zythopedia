import React, { Component } from 'react';
import { API_ROOT } from '../../data/apiConfig';
import { ReactstrapSelect } from 'reactstrap-formik';
import { Field } from 'formik';

class SelectList extends Component {
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
    let baseUrl = `${API_ROOT}/public/lists/` + this.props.listName + '/all';

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
    const name = this.props.name;
    const label = this.props.label;

    if (isLoading) {
      return (
        <div>Loading...</div>
      );
    }

    return (
      <Field
        label={label}
        name={name}
        component={ReactstrapSelect}
        inputprops={{
          name: {name},
          id: {name},
          options: items,
          defaultOption: "-"
        }}
      />
    );
  }
}

export default SelectList;
