import React from 'react';
import Rotas from './rotas';

import '../custom.css'
import Navbar from '../components/navbar';

class App extends React.Component {

  render() {
    return (
      <>
      <Navbar/>
      <div className='container'>
        <Rotas/>
      </div>
      </>
    )
  }
}

export default App;
