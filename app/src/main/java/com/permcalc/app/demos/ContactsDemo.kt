package com.permcalc.app.demos

import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Reads the device contact list: name, one phone number, and one email each. */
suspend fun runContactsDemo(context: Context): ContactsResult = withContext(Dispatchers.IO) {
    val resolver = context.contentResolver

    // contactId -> email
    val emails = HashMap<String, String>()
    resolver.query(
        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
        ),
        null, null, null,
    )?.use { c ->
        val idIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
        val addrIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)
        while (c.moveToNext()) {
            val id = c.getString(idIdx) ?: continue
            val addr = c.getString(addrIdx) ?: continue
            if (!emails.containsKey(id)) emails[id] = addr
        }
    }

    val seen = HashSet<String>()
    val contacts = ArrayList<ContactItem>()
    resolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        ),
        null, null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC",
    )?.use { c ->
        val idIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val nameIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numIdx = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (c.moveToNext()) {
            val id = c.getString(idIdx) ?: continue
            if (!seen.add(id)) continue
            val name = c.getString(nameIdx) ?: continue
            val number = c.getString(numIdx)
            contacts.add(ContactItem(name = name, phone = number, email = emails[id]))
        }
    }

    ContactsResult(contacts)
}
