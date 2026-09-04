import React from 'react';
import { View, StyleSheet } from 'react-native';
import { CalcButton } from './CalcButton';
import { calcColors } from '../../theme';

const D = calcColors.digit;
const O = calcColors.operator;
const E = calcColors.equals;
const S = calcColors.special;

export function ButtonGrid({ calc }) {
  const { inputDigit, inputDecimal, clear, backspace, toggleSign, percentage, handleOperator, equals, operator } = calc;

  return (
    <View style={styles.grid}>
      <View style={styles.row}>
        <CalcButton label="AC" onPress={clear} color={S} textColor="#ef5350" />
        <CalcButton label="+/-" onPress={toggleSign} color={S} textColor="#4fc3f7" fontSize={18} />
        <CalcButton label="%" onPress={percentage} color={S} textColor="#4fc3f7" />
        <CalcButton label="÷" onPress={() => handleOperator('/')} color={operator === '/' ? '#4fc3f7' : O} textColor={operator === '/' ? '#12121f' : '#4fc3f7'} />
      </View>
      <View style={styles.row}>
        <CalcButton label="7" onPress={() => inputDigit('7')} color={D} />
        <CalcButton label="8" onPress={() => inputDigit('8')} color={D} />
        <CalcButton label="9" onPress={() => inputDigit('9')} color={D} />
        <CalcButton label="×" onPress={() => handleOperator('*')} color={operator === '*' ? '#4fc3f7' : O} textColor={operator === '*' ? '#12121f' : '#4fc3f7'} />
      </View>
      <View style={styles.row}>
        <CalcButton label="4" onPress={() => inputDigit('4')} color={D} />
        <CalcButton label="5" onPress={() => inputDigit('5')} color={D} />
        <CalcButton label="6" onPress={() => inputDigit('6')} color={D} />
        <CalcButton label="−" onPress={() => handleOperator('-')} color={operator === '-' ? '#4fc3f7' : O} textColor={operator === '-' ? '#12121f' : '#4fc3f7'} />
      </View>
      <View style={styles.row}>
        <CalcButton label="1" onPress={() => inputDigit('1')} color={D} />
        <CalcButton label="2" onPress={() => inputDigit('2')} color={D} />
        <CalcButton label="3" onPress={() => inputDigit('3')} color={D} />
        <CalcButton label="+" onPress={() => handleOperator('+')} color={operator === '+' ? '#4fc3f7' : O} textColor={operator === '+' ? '#12121f' : '#4fc3f7'} />
      </View>
      <View style={styles.row}>
        <CalcButton label="0" onPress={() => inputDigit('0')} color={D} flex={2} />
        <CalcButton label="." onPress={inputDecimal} color={D} />
        <CalcButton label="=" onPress={equals} color={E} textColor="#ffffff" />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  grid: {
    padding: 8,
  },
  row: {
    flexDirection: 'row',
  },
});
