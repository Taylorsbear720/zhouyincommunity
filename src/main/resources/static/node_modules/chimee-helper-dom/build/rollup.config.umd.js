import base from './rollup.config.base';
const { name } = require('../package.json');
import { camelize } from 'toxic-utils';
const config = base('umd');
export default Object.assign(config, {
  output: {
    format: 'umd',
    file: 'lib/index.browser.js',
    name: camelize(name, true),
  },
});
