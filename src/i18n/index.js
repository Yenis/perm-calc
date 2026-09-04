import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { getLocales } from 'expo-localization';

import en from './locales/en.json';
import de from './locales/de.json';
import bs from './locales/bs.json';

const deviceLang = getLocales()[0]?.languageCode ?? 'en';
const supportedLangs = ['en', 'de', 'bs'];
const fallback = supportedLangs.includes(deviceLang) ? deviceLang : 'en';

i18n
  .use(initReactI18next)
  .init({
    compatibilityJSON: 'v3',
    resources: { en: { translation: en }, de: { translation: de }, bs: { translation: bs } },
    lng: fallback,
    fallbackLng: 'en',
    interpolation: { escapeValue: false },
  });

export default i18n;
