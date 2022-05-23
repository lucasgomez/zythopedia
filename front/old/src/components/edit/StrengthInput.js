import { Component } from 'react';
import { ReactstrapRadio } from 'reactstrap-formik';
import { Field } from 'formik';

class StrengthInput extends Component {

  render() {
    const name = this.props.name;
    const label = this.props.label;

    return (
      <div>
        <label htmlFor={name} style={{ display: 'block' }}>
          {label}
        </label>
        <Field
            name={name}
            component={ReactstrapRadio}
            value=""
            type="radio"
            label="-"
          />
        <Field
            name={name}
            component={ReactstrapRadio}
            value="1"
            type="radio"
            label="Faible"
          />
        <Field
            name={name}
            component={ReactstrapRadio}
            value="2"
            type="radio"
            label="Moyenne"
          />
        <Field
            name={name}
            component={ReactstrapRadio}
            value="3"
            type="radio"
            label="Forte"
          />
      </div>
    );
  }
}

export default StrengthInput;
