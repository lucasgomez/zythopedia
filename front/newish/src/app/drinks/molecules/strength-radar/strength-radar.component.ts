import { AfterViewInit, Component, ElementRef, Input, ViewChild } from '@angular/core';
import { DetailedDrink } from '../../../shared/models/Drink';

const CANVAS_SIZE = 200;
const TARGET_RADIUS = 80;

@Component({
    selector: 'app-strength-radar[drink]',
    template: `
        <canvas #radar width="${CANVAS_SIZE}" height="${CANVAS_SIZE}"></canvas>`
})
export class StrengthRadarComponent implements AfterViewInit {

    @ViewChild('radar', { static: false })
    canvas!: ElementRef<HTMLCanvasElement>;
    ctx?: CanvasRenderingContext2D;

    @Input() drink!: DetailedDrink;

    constructor() {
    }

    ngAfterViewInit(): void {
        this.ctx = this.canvas.nativeElement.getContext('2d') ?? undefined;
        this.updateCanvas();
    }

    private updateCanvas(): void {
        const maxValue = 3;


        const values: Val = [
            { name: 'Amertume', value: this.drink.bitterness },
            { name: 'Houblonnage', value: this.drink.hoppiness },
            { name: 'Douceur', value: this.drink.sweetness },
            { name: 'Acidité', value: this.drink.sourness }
        ];

        const center: Vec = {
            x: CANVAS_SIZE / 2,
            y: CANVAS_SIZE / 2
        };
        const stepRadius = TARGET_RADIUS / maxValue;
        const labelRadius = TARGET_RADIUS + 7;

        this.drawTarget('rgb(0, 163, 193)', center, TARGET_RADIUS);
        this.drawValuesArea(values, 'rgb(139, 198, 0)', center, stepRadius);
        this.fadeTarget(center, stepRadius, maxValue);
        this.drawRuler(values.length, 'rgb(255, 255, 255)', center, TARGET_RADIUS);
        this.writeLabels(values, 'rgb(0, 0, 0)', center, labelRadius);
        this.drawValuesPoint(values, 'rgb(139, 198, 0)', center, stepRadius);
    }

    private fadeTarget(center: Vec, stepRadius: number, maxValue: number) {
        if (!this.ctx) return;

        this.ctx.fillStyle = 'rgb(255, 255, 255)';
        this.ctx.globalAlpha = 0.2;
        for (let i = 1; i <= maxValue; ++i) {
            this.ctx.beginPath();
            this.ctx.arc(center.x, center.y, stepRadius * i, 0, Math.PI * 2, true);
            this.ctx.fill();
        }
        this.ctx.globalAlpha = 1;
    }

    private writeLabels(values: Val, textColor: string, center: Vec, distanceFromCenter: number) {
        if (!this.ctx) return;

        this.ctx.font = '12px sans-serif';
        this.ctx.strokeStyle = textColor;
        this.ctx.textBaseline = 'middle';
        this.ctx.textAlign = 'center';

        const baseAngle = Math.PI * 2 / values.length;
        let totalAngle = undefined;
        let rotation = undefined;
        let position = undefined;

        for (let i = 0; i < values.length; ++i) {
            this.ctx.save();

            totalAngle = baseAngle * i;
            if (Math.PI / 2 < totalAngle && totalAngle < 3 * Math.PI / 2) {
                rotation = baseAngle * i + Math.PI;
                position = {
                    x: center.x,
                    y: center.y + distanceFromCenter
                };
            } else {
                rotation = baseAngle * i;
                position = {
                    x: center.x,
                    y: center.y - distanceFromCenter
                };
            }

            this.ctx.translate(center.x, center.y);
            this.ctx.rotate(rotation);
            this.ctx.translate(-1 * center.x, -1 * center.y);

            this.ctx.strokeText(values[i].name, position.x, position.y);
            this.ctx.restore();
        }
    }

    private drawValuesArea(values: Val, color: string, center: Vec, stepRadius: number) {
        this.drawValues(values, color, true, center, stepRadius);
    }

    private drawValuesPoint(values: Val, color: string, center: Vec, stepRadius: number) {
        this.drawValues(values, color, false, center, stepRadius);
    }

    private drawValues(values: Val, color: string, isArea: boolean, center: Vec, stepRadius: number) {
        if (!this.ctx) return;

        this.ctx.lineJoin = 'round';
        this.ctx.fillStyle = color;
        const baseAngle = Math.PI * 2 / values.length;

        if (isArea) this.ctx.beginPath();
        for (let i = 0; i < values.length; ++i) {
            const summit = this.calculateCoordinates(baseAngle * i, stepRadius * values[i].value, center);
            if (isArea) {
                i === 0 ?
                    this.ctx.moveTo(summit.x, summit.y) :
                    this.ctx.lineTo(summit.x, summit.y);
            } else {
                this.ctx.beginPath();
                this.ctx.arc(summit.x, summit.y, 3, 0, Math.PI * 2, true);
                this.ctx.fill();
            }
        }
        if (isArea) this.ctx.fill();
    }

    drawTarget(color: string, center: Vec, radius: number) {
        if (!this.ctx) return;

        this.ctx.beginPath();
        this.ctx.fillStyle = color;
        this.ctx.arc(center.x, center.y, radius, 0, Math.PI * 2, true);
        this.ctx.fill();
    }

    drawRuler(nbValues: number, color: string, center: Vec, targetRadius: number) {
        if (!this.ctx) return;

        this.ctx.strokeStyle = color;

        const baseAngle = Math.PI * 2 / nbValues;
        for (let i = 0; i < nbValues; ++i) {
            const summit = this.calculateCoordinates(baseAngle * i, targetRadius, center);
            this.ctx.beginPath();
            this.ctx.moveTo(center.x, center.y);
            this.ctx.lineTo(summit.x, summit.y);
            this.ctx.stroke();
        }
    }

    private calculateCoordinates(angle: number, radius: number, origin: Vec) {
        return {
            x: (Math.cos(angle - Math.PI / 2) * radius) + origin.x,
            y: (Math.sin(angle - Math.PI / 2) * radius) + origin.y
        };
    }
}

interface Vec {x: number, y: number}
type Val = Array<{name: string, value: number}>
