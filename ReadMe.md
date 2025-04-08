<b>ANFORDERUNG & VERSTÄNDNIS</b>
<p>Anforderung hier ist zu sagen ob die bekommene PDF-Datei enthält IBANs die Blacklisted sind.</p>

Es geht also darum durch eine zur Verfügng gestellte URL eine PDF-Datei herunterzuladen, Inhalte zu lesen und nach die IBANs suchen um zu testen ob irgendwelche blacklisted wurde.

<b>Verständnis den Anforderungen:</b>
<ul>
    <li>Es gibt feste definierte Liste mit IBANs die Blacklisted sind und damit werden die aus PDF extrarierte IBANs vergliechen</li>
    <li>IBAN muss ein Valid IBAN sein</li>
</ul>

<b>Punkte zu achten :</b>
<ul>
    <li>IBAN Struktur : 2 Buchstaben des Landes wo es herkommt  gefolgt mit Zahlen</li>
    <li>IBAN könnte nach den zwei Buchstaben leere zeichen enthalten oder nicht</li>
    <li>IBAN Länge variert von einem Land zu anderem. Also eine Valid IBAN-Länge in einem Land könnte invalid in einem anderen sein</li>
    <li>IBAN muss immer die 2 Buchstaben des Landes haben. Also für Deutschland z.b ist DE, Frankreich FR, etc...</li>
</ul>

<p><b>KONZEPTION</b></p>
Damit es einfacher sein Änderungen/Weiterentwicklung zu machen ist die große Menge der Konzeption mit Config gedacht.
Wir haben also folgende Objectes  in application.yml:
<ol>
    <li><b>default-iban-properties</b> : enthält country, size und withWhiteSpace. Das sind die Default Iban Config die verwenden werden um IBAN zu prüfen</li>
    <li><b>countrie-iban-properties</b> : hier sind die IBAN-Spezifikation pro Land definiert. code steht für die zwei Buchstaben Anfangs der IBAN, withWhiteSpace sagt uns ob die Size mit Leerzeichen berechnen sein muss oder nicht</li>
    <li><b>black-listed</b> : Enthält die Liste von Landes die wir blacklisten wollen</li>
    <li><b>check-specification</b> : Hier wollen die Möglichkeit vorbereiten in der Zukunft nicht nur IBAN prüfen zu können sondern andere Infos wie BLZ, KONTO,etc... das spezifizieren in der Attribute <i>activeElementsToCheck</i><br><i>elements</i> enthält also die zu prüfende Daten bei jedem Element.
        <ul>
            <li><b>name</b> : Name des Elements zu prüfen . z.b : IBAN, BIC, KONTO,etc...</li>
            <li><b>initialString</b>: Die Daten aus PDF-Datei kommen in Form von Block-Text für jede Seite. Drin müssen wir die benötigte Info suchen und extrahieren. Deswegen versuchen wir das mit initial- und lastString zu schneiden </li>
            <li><b>minSize und maxSize</b>: Länge zu respektieren. </li>
            <li><b>blacklisted</b>: feste definierte blacklisted Elements. Z.b: IBAN, Bank Name, BIC,etc...</li>
        </ul>    
    </li>
</ol>

<p>UMSETZUNG</p>
In der Umsetzung prüfen wir ob :
    <ol>
        <li> das gegebene Land sich in der Blackliste nicht befindet</li>
        <li> Element respektiert die definierte Länge</li>
        <li> Wenn element eine IBAN ist, Element respektiert die Land-Buchstaben </li>
        <li> Element sich in der Blackliste nicht befindet</li>
    </ol>
<p>Wenn es keine IBAN-verification ist (z.b: BIC, KONTO,etc..) ind nur die Länge (Specification) und isBlacklisted getestet.</p>

<p>Da wir die Message in ResponseEntity darstellen müssen versuchen wir es in StringBuilder zu bauen und zurückzugeben.<br>
    Wir haben einige Länder fürs Test definiert. 
</p>

<p>Die PDF-Datei ist heruntergeladen und in einer Ordner <i>static</i> gespeichert. Wir generieren eine Unique-Name für jede heruntergeladene Datei.</p>
<p>Nach dem Daten-Extrahieren ist die Datei gelöscht damit wir die Speicher nicht überfordern.</p>
<p>Wir haben einige Tests geschieben. <br> Es gibt natürlich noch viele Test-Möglichkeiten<br> An der Stelle macht nicht viel Sinn Mock-Test zu schreiben weil es relativ wenig ist und wir Lokale Testdaten zur Verfügung haben </p>

<p><b>SETUP</b></p>
Entwicklung wurde in Intellij IDEA 2023.1.2 gemacht
Es wurde dafür ein Repository erstellt :<br>
<b>Repo link</b> <a href="https://github.com/Mibrai/pdfApi.git">https://github.com/Mibrai/pdfApi.git</a> <br>
Es gibt auf dem Repo zwei Branchs :
    <ul>
        <li>Master</li>
        <li>feature</li>
    </ul> <br>
Regelmäßige Umsetzungen sind in dem Feature-Branch gemacht und danach durch ein Pull Request in dem Master-Branch gemergt.
Das Repo ist Public Sie können also auschecken und Lokal starten.


<b>Ausführen :</b>
Wir haben gerade nur POST Methode im Controller. Deswegen können Sie ruhig mit Postman abfragen.<br>
Um die unterschiedliche Verhalten zu sehen können Sie mit Abfrage-Parameter rumspielen :
z.b: 
    <ul>
        <li>countryCode : FR </li>
        <li>countryCode : DE </li>
        <li>countryCode : EN </li>
    </ul>

Sie können auch mit Daten in application.yml rumtanzen und sehen wie es reagiert.<br>
<br/>
Um zu testen haben wir die Testdata_Invoices.pdf in einem privaten Domän (<a>room4-solutions.com</a>) hochgeladen.<br>
Link POST Request in Postman z.b <a> localhost:8079/checkBlacklistedElements?pdfDownloadUrl=room4-solutions.com/testApi/Testdata_Invoices.pdf&countryCode=DE</a>
<br/>
Test Parameters für Post Request:
<ul>
    <li><table><tr>
        <td>pdfDownloadUrl</td>
        <td>room4-solutions.com/testApi/Testdata_Invoices.pdf</td>
    </tr></table></li>
    <li><table><tr>
        <td>countryCode</td>
        <td>DE</td>
    </tr></table></li>
</ul>

<p><b>MÖGLICHE ZUKUNFTIGE VERBESSERUNGEN</b></p>
<ul>
    <li>Richtige Model erstellen das mit die ganze PDF-Struktur matchen könnte um mehr Elements testen zu können und damit auch die Implementierung besser Modularisieren zu können <br> Dadurch wäre z.b möglich alle Einträge, Rechnungnummer,Rechnungsdatum, etc... zu prüfen <br> In dem Fall wird eine weiterentwicklung auch leicht </li>
    <li>Weitere Tests Hinzufügen um die Anwendung mehr Robust zu haben</li>
    <li>Bessere Response Status definieren für ResponseEntity. Aktuell haben wir alle über Status 200 zurückgegeben. Das könnte besser personalisiert werden mit Logger z.b </li>
    <li>Refractoring : Code besser organisieren :)</li>
</ul>

