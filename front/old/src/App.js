import React, { Component } from 'react';
import Manager from './components/Manager';
import AllBeersId from './components/AllBeersId';
import BottleBarDisplay from './components/BottleBarDisplay';
import { CookiesProvider } from 'react-cookie';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import TapBeersDisplay from './components/TapBeersDisplay';

class App extends Component {
  render() {
	  return (
      <CookiesProvider>
  			<Router>
            <Switch>
              <Route
                path="/beamer"
                component={BeamerRoute}/>
              <Route
                path="/beamerbottle"
                component={BeamerBottleRoute}/>
              <Route
                path="/cards"
                component={AllBeersId}/>
              <Route
                path="/bottletv"
                component={BottleBarDisplay}/>
              <Manager/>
            </Switch>
  			</Router>
      </CookiesProvider>
    );
  }
}

const BeamerRoute = ({ match }) => (
  <div>
    <TapBeersDisplay
      listId={666}
      listName={"bars"}/>
  </div>
);

const BeamerBottleRoute = ({ match }) => (
  <div>
    <TapBeersDisplay
      listId={667}
      listName={"bars"}/>
  </div>
);

export default App;
