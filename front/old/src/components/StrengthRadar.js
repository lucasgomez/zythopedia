import React, { Component } from 'react';

class StrengthRadar extends Component {
  componentDidMount() {
    this.updateCanvas();
  }

  updateCanvas() {
    const ctx = this.refs.canvas.getContext('2d');

    const maxValue = 3;
    const canvasSize = 150;
    const targetRadius = 60;

    var values = [{
        name: 'Amertume', value: this.props.bitterness
      },{
        name: 'Houblonnage', value: this.props.hopping
      },{
        name: 'Douceur', value: this.props.sweetness
      },{
        name: 'Acidité', value: this.props.sourness
      }
    ];

    var center = {
      x: canvasSize / 2,
      y: canvasSize / 2
    };
    var stepRadius = targetRadius / maxValue;
    var labelRadius = targetRadius + 7;

    this.drawTarget(ctx, 'rgb(0, 163, 193)', center, targetRadius);
    this.drawValuesArea(ctx, values, 'rgb(139, 198, 0)', center, stepRadius);
    this.fadeTarget(ctx, center, stepRadius, maxValue);
    this.drawRuler(ctx, values.length, 'rgb(255, 255, 255)', center, targetRadius);
    this.writeLabels(ctx, values, 'rgb(0, 0, 0)', center, labelRadius);
    this.drawValuesPoint(ctx, values, 'rgb(139, 198, 0)', center, stepRadius);
  }

  fadeTarget(ctx, center, stepRadius, maxValue) {
    ctx.fillStyle = 'rgb(255, 255, 255)';
    ctx.globalAlpha = 0.2;
    for (var i = 1; i <= maxValue; i++) {
      ctx.beginPath();
      ctx.arc(center.x, center.y, stepRadius * i, 0, Math.PI * 2, true);
      ctx.fill();
    }
    ctx.globalAlpha = 1;
  }

  writeLabels(ctx, values, textColor, center, distanceFromCenter) {
    ctx.font = '10px sans-serif';
    ctx.strokeStyle = textColor;
    ctx.textBaseline = 'middle';
    ctx.textAlign = 'center';

    var baseAngle = Math.PI * 2 / values.length;
    var totalAngle = undefined;
    var rotation = undefined;
    var position = undefined;

    for (var i = 0; i < values.length; i++) {
      ctx.save();

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

      ctx.translate(center.x, center.y);
      ctx.rotate(rotation);
      ctx.translate(-1 * center.x, -1 * center.y);

      ctx.strokeText(values[i].name, position.x, position.y);
      ctx.restore();
    }
  }

  drawValuesArea(ctx, values, color, center, stepRadius) {
    this.drawValues(ctx, values, color, true, center, stepRadius);
  }

  drawValuesPoint(ctx, values, color, center, stepRadius) {
    this.drawValues(ctx, values, color, false, center, stepRadius);
  }

  drawValues(ctx, values, color, isArea, center, stepRadius) {
    ctx.lineJoin = 'round';
    ctx.fillStyle = color;
    var baseAngle = Math.PI * 2 / values.length;
    var summit = undefined;

    if (isArea)
      ctx.beginPath();

    for (var i = 0; i < values.length; i++) {
      summit = this.calculateCoordinates(baseAngle * i, stepRadius * values[i].value, center);

      if (isArea) {
        i === 0 ?
          ctx.moveTo(summit.x, summit.y) :
          ctx.lineTo(summit.x, summit.y);
      } else {
        ctx.beginPath();
        ctx.arc(summit.x, summit.y, 3, 0, Math.PI * 2, true);
        ctx.fill();
      }
    }
    if (isArea)
      ctx.fill();
  }

  drawTarget(ctx, color, center, radius) {
    ctx.beginPath();
    ctx.fillStyle = color;
    ctx.arc(center.x, center.y, radius, 0, Math.PI * 2, true);
    ctx.fill();
  }

  drawRuler(ctx, nbValues, color, center, targetRadius) {
    ctx.strokeStyle = color;

    var baseAngle = Math.PI * 2 / nbValues;

    for (var i = 0; i < nbValues; i++) {
      var summit = this.calculateCoordinates(baseAngle * i, targetRadius, center);
      ctx.beginPath();
      ctx.moveTo(center.x, center.y);
      ctx.lineTo(summit.x, summit.y);
      ctx.stroke();
    }
  }
  calculateCoordinates(angle, radius, origin) {
    return {
      x: (Math.cos(angle - Math.PI / 2) * radius) + origin.x,
      y: (Math.sin(angle - Math.PI / 2) * radius) + origin.y
    };
  }

  render() {
    return ( <
      canvas ref = "canvas"
      width = {
        150
      }
      height = {
        150
      }
      />
    );
  }
}

export default StrengthRadar;
