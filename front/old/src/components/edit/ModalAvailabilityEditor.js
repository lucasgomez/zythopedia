import { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, ButtonGroup, Dropdown, DropdownButton, Modal } from 'react-bootstrap';
import Emoji from '../Emoji';
import axios from 'axios';
import { API_ROOT } from '../../data/apiConfig';

class ModalAvailabilityEditor extends Component {

  constructor(props: any) {
    super(props);

    this.state = {
      beerToUpdate: this.props.beerToUpdate,
    };
    this.handleCloseModal = this.handleCloseModal.bind(this);
  }

  componentWillReceiveProps(nextProps) {
    this.setState({beerToUpdate: nextProps.beerToUpdate});
  }

  submitAvailability(beer, oldStatus, newStatus, service) {
    if (window.confirm('Etes-vous sur de vouloir changer le status de "'+beer.name+'" de "'+oldStatus+'" à "'+newStatus+'"?'))
      this.setAvailabilityWithGet(beer.id, newStatus, service);
  }

  setAvailability(beerId, status, service) {
    let postBottleAvailabilityUrl = `${API_ROOT}/private/beers/` + beerId + '/' +service + '/availability';

    var self = this;
    axios.put(
      postBottleAvailabilityUrl,
      status,
      { withCredentials: true,
        headers: {"Content-Type": "text/plain"}})
    .then(function (response){
      self.props.handleCloseModal();
      self.props.onClose();
     }).catch(function (error) {
       console.log(error);
       alert("Ebriété assumée, erreur assurée bis");
     }
    );
  }

  setAvailabilityWithGet(beerId, status, service) {
    var self = this;
    axios.get(
      `${API_ROOT}/private/beers/` + beerId + '/' +service + '/availabilitybis',
      {
        params: {
          availability: status
        },
        withCredentials: true,
        headers: {"Content-Type": "text/plain"}
      })
    .then(response => self.setState({beerToUpdate: null}));
  }

  handleCloseModal() {
    this.setState({beerToUpdate: null});
  }

  render() {
    const {
      beerToUpdate,
    } = this.state;

    if (!beerToUpdate)
      return <div/>;

    let tapAvailability =
      <DropdownButton as={ButtonGroup} title="Pression" id="bg-nested-dropdown-tapAvailability">
        {availabilities.map((availability: any) =>
            beerToUpdate.tapAvailability !== availability.status &&
              <Dropdown.Item
                onClick={() => this.submitAvailability(beerToUpdate, beerToUpdate.tapAvailability, availability.status, 'tap')}
                key={'actionButtonsList-'+beerToUpdate.id+'-tap-statuses-'+availability.status}>
                  {availability.label}
              </Dropdown.Item>
        )}
      </DropdownButton>

    let bottleAvailability =
      <DropdownButton as={ButtonGroup} title="Bouteille" id="bg-nested-dropdown-tapAvailability">
        {availabilities.map((availability: any) =>
            beerToUpdate.bottleAvailability !== availability.status &&
              <Dropdown.Item
                onClick={() => this.submitAvailability(beerToUpdate, beerToUpdate.bottleAvailability, availability.status, 'bottle')}
                key={'actionButtonsList-'+beerToUpdate.id+'-bottle-statuses-'+availability.status}>
                  {availability.label}
              </Dropdown.Item>
        )}
      </DropdownButton>

    return (
      <Modal show={beerToUpdate} onHide={this.handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>{"Modification de la disponibilité de "+beerToUpdate.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p><Link to={'/edit/beer/'+beerToUpdate.id}><Emoji symbol="✏" label="Edition"/> Modifier {beerToUpdate.name}</Link></p>
          <p>Mes excuses aux familles etc. pour cette ergonomie de merde.</p>
          {beerToUpdate.bottleSellingPrice && bottleAvailability}
          {beerToUpdate.tapPriceBig && tapAvailability}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={this.handleCloseModal}>
            Annuler
          </Button>
        </Modal.Footer>
      </Modal>
    );
  }
}

const availabilities = [
  {status : 'NOT_YET_AVAILABLE', label: 'Pas encore en service'},
  {status : 'AVAILABLE', label: 'En service'},
  {status : 'NEARLY_OUT_OF_STOCK', label: 'Bientôt épuisé'},
  {status : 'OUT_OF_STOCK', label: 'Epuisé'}
];


export default ModalAvailabilityEditor;
