import React, { Component } from 'react';
import { API_ROOT } from '../../data/apiConfig';
import { Field } from 'formik';

class BarsCheckboxes extends Component {
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
    let baseUrl = `${API_ROOT}/public/lists/bars`;

    fetch(baseUrl)
      .then(response => response.json())
      .then(items =>
        this.setState({
          items: items,
          isLoading: false
        })
      );
  }

  renderCheckbox = (groupName, value, label) => {
    return (
      <Field name={groupName}>
        {({ field, form }) => (
          <label>
            <input
              type="checkbox"
              checked={field.value.includes(value)}
              onChange={() => {
                if (field.value.includes(value)) {
                  const nextValue = field.value.filter(
                    value2 => value2 !== value
                  );
                  form.setFieldValue(groupName, nextValue);
                } else {
                  const nextValue = field.value.concat(value);
                  form.setFieldValue(groupName, nextValue);
                }
              }}
            />
            {label}
          </label>
        )}
      </Field>
    );
  }

  render() {
    const {
      items,
      isLoading
    } = this.state;

    const groupName = this.props.groupName;

    if (isLoading || !items || items.length === 0) {
      return (
        <div>Loading...</div>
      );
    }

    return (
      <div>
        {items.map((item: any) =>
          this.renderCheckbox(groupName, item.id, item.name)
        )}
      </div>
    );
  }
}

export default BarsCheckboxes;
