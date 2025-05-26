import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Availability} from "../../models/Availability";

@Component({
  selector: 'availability-badge',
  templateUrl: './availibility-badge.component.html',
  styleUrls: ['./availibility-badge.component.css']
})
export class AvailibilityBadgeComponent {

  @Input() availability!: Availability;
  @Input() location?: string;
  @Input() showAvailabilityMenu = false;
  @Output() clickToChangeAvailability: EventEmitter<Availability> = new EventEmitter<Availability>();

  availabilityOptions: Availability[] = ['AVAILABLE', 'OUT_OF_STOCK', 'SOON'];
  popupVisible = false;

  get otherAvailabilities(): Availability[] {
    return this.availabilityOptions.filter(option => option !== this.availability);
  }

  onClick(): void {
    if (this.clickToChangeAvailability) {
      this.popupVisible = true;
    }
  }

  changeAvailability(newAvailability: Availability): void {
    this.clickToChangeAvailability?.emit(newAvailability);
    this.popupVisible = false;
  }

}
