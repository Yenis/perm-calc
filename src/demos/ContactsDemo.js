import * as Contacts from 'expo-contacts';

export async function runContactsDemo() {
  const { data } = await Contacts.getContactsAsync({
    fields: [
      Contacts.Fields.Name,
      Contacts.Fields.PhoneNumbers,
      Contacts.Fields.Emails,
    ],
    sort: Contacts.SortTypes.FirstName,
  });

  const contacts = data
    .filter(c => c.name)
    .map(c => ({
      name: c.name,
      phone: c.phoneNumbers?.[0]?.number ?? null,
      email: c.emails?.[0]?.email ?? null,
    }));

  return { contacts, templateVars: { count: contacts.length } };
}
