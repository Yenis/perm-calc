import { useState, useCallback } from 'react';

const MAX_DIGITS = 12;

export function useCalculator() {
  const [display, setDisplay] = useState('0');
  const [prevValue, setPrevValue] = useState(null);
  const [operator, setOperator] = useState(null);
  const [waitingForOperand, setWaitingForOperand] = useState(false);

  const inputDigit = useCallback((digit) => {
    if (waitingForOperand) {
      setDisplay(String(digit));
      setWaitingForOperand(false);
    } else {
      setDisplay(prev =>
        prev === '0' ? String(digit) : prev.length < MAX_DIGITS ? prev + digit : prev
      );
    }
  }, [waitingForOperand]);

  const inputDecimal = useCallback(() => {
    if (waitingForOperand) {
      setDisplay('0.');
      setWaitingForOperand(false);
      return;
    }
    if (!display.includes('.')) {
      setDisplay(prev => prev + '.');
    }
  }, [display, waitingForOperand]);

  const clear = useCallback(() => {
    setDisplay('0');
    setPrevValue(null);
    setOperator(null);
    setWaitingForOperand(false);
  }, []);

  const backspace = useCallback(() => {
    if (waitingForOperand) return;
    setDisplay(prev => (prev.length > 1 ? prev.slice(0, -1) : '0'));
  }, [waitingForOperand]);

  const toggleSign = useCallback(() => {
    setDisplay(prev => String(parseFloat(prev) * -1));
  }, []);

  const percentage = useCallback(() => {
    setDisplay(prev => String(parseFloat(prev) / 100));
  }, []);

  const handleOperator = useCallback((nextOp) => {
    const current = parseFloat(display);
    if (operator && !waitingForOperand) {
      const result = compute(prevValue, current, operator);
      const formatted = formatResult(result);
      setDisplay(formatted);
      setPrevValue(result);
    } else {
      setPrevValue(current);
    }
    setWaitingForOperand(true);
    setOperator(nextOp);
  }, [display, operator, prevValue, waitingForOperand]);

  const equals = useCallback(() => {
    if (!operator || waitingForOperand) return;
    const current = parseFloat(display);
    const result = compute(prevValue, current, operator);
    const formatted = formatResult(result);
    setDisplay(formatted);
    setPrevValue(null);
    setOperator(null);
    setWaitingForOperand(true);
  }, [display, operator, prevValue, waitingForOperand]);

  return { display, operator, inputDigit, inputDecimal, clear, backspace, toggleSign, percentage, handleOperator, equals };
}

function compute(a, b, op) {
  switch (op) {
    case '+': return a + b;
    case '-': return a - b;
    case '*': return a * b;
    case '/': return b !== 0 ? a / b : 0;
    default: return b;
  }
}

function formatResult(value) {
  if (!isFinite(value)) return '0';
  const str = String(value);
  if (str.length > MAX_DIGITS) {
    return parseFloat(value.toPrecision(8)).toString();
  }
  return str;
}
