import React, { Component } from 'react';
import { Link } from 'react-router-dom';

class Welcome extends Component {
  render() {
    return (
      <div className="container">
        <h2>Zythopedia, <i>nom propre</i>, du grec <i>zythos-</i> bière, <i>-pedos</i> éducation</h2>

        <p>Bienvenue dans la taverne de la Connaissance sur la Bière! Vous trouverez ici toutes les informations
        pour choisier vos boissons à la <strong>Fête de la Bière.</strong></p>

        <p>Le menu ci-dessus vous aidera à trouver les bières :</p>

        <ul>
          <li>
            Par bar, par exemple le <Link to={'/list/bars/666'}>bar pressions</Link> ou le <Link to={'/list/bars/667'}>bar bouteille</Link>
          </li>
          <li>
            Par couleur, par exemple les <Link to={'/list/colors/32'}>bières brunes</Link> ou les <Link to={'/list/colors/1664'}>bières blanches</Link>
          </li>
          <li>
            Par style, par exemple les <Link to={'/list/styles/14'}>IPA</Link> ou les <Link to={'/list/styles/27'}>Sans alcool</Link>
          </li>
        </ul>

      </div>
    );
  }
}

export default Welcome;
