import {Component, Input} from '@angular/core';
import {Availability} from "../../models/Availability";

@Component({
  selector: 'availability-badge',
  templateUrl: './availibility-badge.component.html',
  styleUrls: ['./availibility-badge.component.css']
})
export class AvailibilityBadgeComponent {

  @Input() availability!: Availability;

}
