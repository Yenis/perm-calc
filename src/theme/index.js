import { MD3DarkTheme } from 'react-native-paper';

export const theme = {
  ...MD3DarkTheme,
  colors: {
    ...MD3DarkTheme.colors,
    primary: '#4fc3f7',
    primaryContainer: '#1a3a4a',
    secondary: '#ef5350',
    secondaryContainer: '#4a1a1a',
    surface: '#1e1e2e',
    surfaceVariant: '#2a2a3e',
    background: '#12121f',
    onBackground: '#e8e8f0',
    onSurface: '#e8e8f0',
    onSurfaceVariant: '#b0b0c8',
    outline: '#3a3a52',
    elevation: {
      level0: 'transparent',
      level1: '#1e1e2e',
      level2: '#22223a',
      level3: '#262646',
      level4: '#2a2a4e',
      level5: '#2e2e56',
    },
  },
};

export const calcColors = {
  digit: '#2a2a3e',
  operator: '#1a3a4a',
  equals: '#0d47a1',
  special: '#1e1e2e',
  display: '#12121f',
  permButton: '#2a1a3a',
  permButtonBorder: '#6a1b9a',
  warning: '#b71c1c',
  warningBg: '#1a0000',
  legitimate: '#1a3a1a',
  legitimateBorder: '#2e7d32',
  malicious: '#3a1a1a',
  maliciousBorder: '#c62828',
};
