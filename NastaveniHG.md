# Nastavení Handy Geocachingu (dále jen HG) #

## Obecné ##
Zde se nachází základní nastavení nastavení aplikace.

_Přihlašovací údaje na geocaching.com:_
  * Jméno - uživatelské jméno na stránkách Geocaching.com (**neplést s Geocaching.cz**)
  * Heslo - heslo

_Nejbližší keše:_
> Nastavení filtru pro hledání keší. Filtr se aplikuje na straně serveru a to tak, že nejprve dojde k hledání keší a až pak se výsledek hledání filtruje. A protože se filtruje jen první nalezená stránka s kešmi (z historických důvodů), tak je možné, že po vyfiltrování to vrátí prázdný seznam. Z toho vyplívá, že při filtrování se neberou v úvahu nálezy přidané do Field notes a nezalogované na GC.com.

_Počet keší:_
> Počet maximálně vrácených keší po aplikaci filtru. Maximum je 20.

_Potvrzovat dialogy:_
> Ano pro potvrzování dialogů např. při přidání keše, uložení změn waypointu, atd.
> Ne pro automatické zavření dialogu po 800 ms.

_Frekvence blikání:_
> S určitou hodnotou lze na některých telefonech zařídit permanentní rozsvícení displaye. Ale neptejte se jak.

## GPS ##
Nastavení (zatím) interní GPS. Později snad kompletního nastavení spojení (typu) GPS.

_Typ Interní GPS:_
  * Obecný - Základní doporučené nastavení, mělo by fungovat s většinou interních GPS
  * Obecný, 1s aktualizace - To samé jako první volba, ale s vynucenou aktualizací polohy po 1 sekundě
  * Samsung SGH-i5x0 - Volba pro Samsung SGH-i5x0, který nepodporuje získávání informací o kompasu, výšce a rychlosti.
  * BlackBerry - Doporučené nastavení interní GPS pro zařízení BlackBerry.

## Field Notes ##
Nastavení kolem Field Notes a jejich odesílání na stránky Geocaching.com.

_Obecné:_
  * Inkrementální Field Notes - Stránka [Geocaching.com - Field notes](http://www.geocaching.com/my/fieldnotes.aspx) bude akceptovat jen nové ještě neodeslané Field notes namísto všech.
  * Ikonky ve Field notes - Zapne zobrazování informačních ikon v seznamu Field notes, jinak použije textové označení (pro malé displaye / chyby v telefonu, kdy odsazení ikonky a textu je příliš velké)
  * Nejprve název keše - Zobrazuje nejprve název keše a až pak jeho GC kód, jinak obráceně.
  * Zalamovat text - Umožní dvojřádkové zobrazení popisu Field notes namísto jednořádkového.

## Kompas ##
Ponecháno zatím z historického důvodu, doporučuji nepoužívat, resp. nastavit volbu _Použít interní kompas_ na **Ne**.

Původní myšlenka bylo umožnit používání vestavěných magnetických kompasů v telefonech pro navigaci ke keši (jako kompas namísto toho kompasu z GPS), což by umožnilo určení ještě přesnějšího směru ke keši pouhým natočením telefonu. Bohužel situace je jiná:

> _Tak jsem prolezl různé materiály kolem vestavěného magnetického kompasu v Nokia telefonech S60 a věc se má tak, že je to pěkně na ho`***`. A to doslovně. Nejen, že je potřeba dopředu udělat kalibrace pomocí Nokiáckých map, ale je nutné mít Nokiácké mapy spuštěny společně s HG, aby to fungovalo (Kalibraci je nutné provést při každém spuštění Nokiáckých map - lze ji udělat do 3 sekund - viz google). A kromě toho v implementaci toho API na získávání dat z kompasu je chyba, která cca do minuty shodí HG na nedostatek paměti. Takže to je tak ke stavu použití vestavěného kompasu v Nokia telefonech. Zatím sem tam možnost povolit kompas nechal, ale nedoporučuji používat._

_Magnetická deklinace:_
> Určuje odchylku ve stupních magnetického severu od pravého severu (tuto odchylku je nutné přičíst k magnetickému severu, aby jsme dostali pravý sever). Hodnota se liší pro různou polohu na zemi. Příslušnou odchylku lze nalézt na [této stránce](http://www.magnetic-declination.com/). Pro českou republiku je přibližná hodnota cca 3 stupně.

## Go4Cache.com ##
Go4Cache slouží ke sdílení polohy mezi kačery a zobrazení polohy kačerů na [mapě](http://go4cache.com/). Více o této službě se dozvíte na http://go4cache.com/about/. Na stránky Go4Cache není potřeba se registrovat, jediné co je potřeba je mít vyplněný přihlašovací údaje na geocaching.com (stačí jméno) v  HG Nastavení->Obecné a povolit službu.

Aktuální poloha je odeslána jednou za 5 minut. Při navigaci na keš se posílá její GC kód, u waypointu jeho název.

_Sdílet polohu:_
> Ano, pro zasílání polohy. Výchozí nastavení je Ne (ne všichni to chtějí používat).