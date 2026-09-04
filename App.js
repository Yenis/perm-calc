import './src/i18n';
import React, { useState, useEffect } from 'react';
import { View, ActivityIndicator, StyleSheet } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Provider as PaperProvider } from 'react-native-paper';
import { theme } from './src/theme';
import { DisclaimerScreen } from './src/screens/DisclaimerScreen';
import { CalculatorScreen } from './src/screens/CalculatorScreen';

const DISCLAIMER_KEY = '@permcalc_disclaimer_accepted';

export default function App() {
  const [ready, setReady] = useState(false);
  const [disclaimerAccepted, setDisclaimerAccepted] = useState(false);

  useEffect(() => {
    AsyncStorage.getItem(DISCLAIMER_KEY).then(val => {
      setDisclaimerAccepted(val === 'true');
      setReady(true);
    });
  }, []);

  const acceptDisclaimer = async () => {
    await AsyncStorage.setItem(DISCLAIMER_KEY, 'true');
    setDisclaimerAccepted(true);
  };

  if (!ready) {
    return (
      <View style={styles.loader}>
        <ActivityIndicator color="#4fc3f7" size="large" />
      </View>
    );
  }

  return (
    <PaperProvider theme={theme}>
      {disclaimerAccepted ? (
        <CalculatorScreen />
      ) : (
        <DisclaimerScreen onAccept={acceptDisclaimer} />
      )}
    </PaperProvider>
  );
}

const styles = StyleSheet.create({
  loader: {
    flex: 1,
    backgroundColor: '#12121f',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
