// https://docs.expo.dev/guides/using-eslint/
const { defineConfig } = require('eslint/config');
const expoConfig = require('eslint-config-expo/flat');
const baseConfig = require('../eslint.config.js');

module.exports = defineConfig([
  ...expoConfig,
  baseConfig,
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      parser: require('@typescript-eslint/parser'),
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        project: './tsconfig.json',
      },
    },
    settings: {
      'import/resolver': {
        typescript: {
          alwaysTryTypes: true,
          project: './tsconfig.json',
        },
      },
    },
    rules: {
      'import/no-unresolved': 'error',
      // React Native 특화 규칙들이 여기에 올 수 있음
    },
  },
  {
    ignores: ['dist/*', 'node_modules/*', 'ios/*', 'android/*', '.expo/*'],
  },
]);
