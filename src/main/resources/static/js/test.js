import Chimee from 'C:/work/workspace/comunity/node_modules/chimee';
const chimee = new Chimee('#app');
chimee.on('play', () => console.log('play!!'));
chimee.load('http://cdn.toxicjohann.com/lostStar.mp4');
chimee.play()