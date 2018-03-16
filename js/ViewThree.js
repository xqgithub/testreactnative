import React, {Component} from 'react';
import {
    Button
} from 'react-native'

class ViewThree extends Component{

    static navigationOptions = {
        headerTitle:'ViewThree'
    }

    render(){
        return (<Button  title='ViewThree'/>)
    }

}

export default ViewThree;