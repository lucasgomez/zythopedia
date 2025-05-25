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
  @Input() labelAddition = "";
  @Output() click: EventEmitter<null> = new EventEmitter<null>();

  onClick(): void {
    if (this.click) {
      this.click.emit();
    }
  }
}
