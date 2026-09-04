const { getDefaultConfig } = require('expo/metro-config');

const config = getDefaultConfig(__dirname);

// Disable package exports resolution — fixes ESM/CJS issues with i18next and other packages
config.resolver.unstable_enablePackageExports = false;

module.exports = config;
