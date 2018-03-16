import { AppRegistry } from 'react-native'
import {StackNavigator} from 'react-navigation'
//import App from './js/APP'
import ViewOne from './js/ViewOne'
import ViewTow from './js/ViewTow'
import ViewThree from './js/ViewThree'

const App = StackNavigator({
  One:{screen:ViewOne},
  Tow:{screen:ViewTow},
  Three:{screen:ViewThree},
})


AppRegistry.registerComponent('navigation', () => App)