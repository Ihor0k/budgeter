import Big from 'big.js'
// @ts-ignore
import toFormat from 'toformat'

const BigWithFormat = toFormat(Big)

BigWithFormat.format = {
  groupSeparator: ' ',
  decimalSeparator: '.',
  groupSize: 3,
}

BigWithFormat.prototype.toString = function () {
  return this.toFormat(2)
}

export default BigWithFormat
