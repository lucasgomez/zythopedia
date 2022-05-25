import React, { Component } from 'react';
import { Nav } from 'react-bootstrap';
import Emoji from './Emoji';
import { API_ROOT } from '../data/apiConfig';

class LoginManager extends Component {

  constructor(props) {
    super(props);
    this.login = this.login.bind(this);
    this.logout = this.logout.bind(this);
  }

  login() {
    let port = window.location.port ? ':' + window.location.port : '';

    if (port === ':3000') {
      port = ':8080';
    }
    window.location.href = `${API_ROOT}/private`;
  }

  logout() {
    fetch(
      `${API_ROOT}/api/logout`,
      {
        method: 'POST',
        credentials: 'include',
        headers: {'X-XSRF-TOKEN': this.props.csrfToken}
      })
    .then(res => res.json())
    .then(response => {
      alert(response.logoutUrl+' '+response.idToken);
      window.location.href = response.logoutUrl + "?id_token_hint=" +
          response.idToken + "&post_logout_redirect_uri=" + window.location.origin;
    });
  }

  render() {

    const button = this.props.isAuthenticated ?
      <Nav.Link onClick={this.logout}><Emoji symbol="🔒" label="Logout"/></Nav.Link> :
      <Nav.Link onClick={this.login}><Emoji symbol="🔐" label="Login"/></Nav.Link>;
    return (
      <div className={this.props.className}>
        {button}
      </div>
    );
  }
}

export default LoginManager;
