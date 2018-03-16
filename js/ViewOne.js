import React, { Component } from 'react';
import {
    Button,
    View,
    Text,
} from 'react-native'

class ViewOne extends Component {

    static navigationOptions = {
        headerTitle:'ViewOne'
    }

    render() {
        return (
            <View>
                <Button
                    onPress={() => { this.props.navigation.navigate('Tow') }}
                    title='点击进入ViewTow'
                />
            </View>
        )
    }

}

export default ViewOne;