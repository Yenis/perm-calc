import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export function Display({ value, operator }) {
  const fontSize = value.length > 9 ? 32 : value.length > 6 ? 44 : 56;

  return (
    <View style={styles.container}>
      <Text style={styles.operatorHint}>
        {operator ? operatorSymbol(operator) : ' '}
      </Text>
      <Text style={[styles.value, { fontSize }]} numberOfLines={1} adjustsFontSizeToFit>
        {value}
      </Text>
    </View>
  );
}

function operatorSymbol(op) {
  const map = { '+': '+', '-': '−', '*': '×', '/': '÷' };
  return map[op] ?? '';
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'flex-end',
    paddingHorizontal: 20,
    paddingBottom: 8,
    backgroundColor: '#12121f',
  },
  operatorHint: {
    fontSize: 22,
    color: '#4fc3f7',
    marginBottom: 2,
  },
  value: {
    color: '#e8e8f0',
    fontWeight: '200',
    letterSpacing: -1,
  },
});
