# Handy Geocaching #
Handy Geocaching je mobilní javová aplikace pro usnadnění geocachingu. Spojuje mobilní přístup ke stránkám geocaching.com s navigačními funkcemi. Umožňuje vyhledávání keší (nejbližší, podle waypointu, podle klíčového slova) a stahování všech potřebných informací o nich(listing, nápověda, přídavné waypointy, …).

## Stažení aplikace ##
### [Stabilní verze](http://code.google.com/p/handygeocaching/downloads/list?can=2&q=label:Stable%20Featured) ###
Stažení JAD, JAR souborů (instalace přímo z telefonu) [poslední stabilní verze](http://code.google.com/p/handygeocaching/downloads/list?can=2&q=label:Stable%20Featured):
  * [HandyGeocaching.jad](http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/stable/HandyGeocaching.jad)
  * [HandyGeocaching.jar](http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/stable/HandyGeocaching.jar)

Pozor: Na začátku nového roku vypršel podpisový certifikát, kterým je podepsán HandyGeocaching (nový certifikát stojí cca 5700 Kč/rok, o zakoupení neuvažuji). Proto všechny nově vydané verze budou již jen nepodepsané. Rozdíl mezi mezi podepsanou a nepodepsanou verzi je ten, že u podepsané existuje možnost v nastavení aplikací vypnout otravné potvrzení na čtení / zápis souborů (například v novějších Nokiích je toto možné nastavit i u nepodepsaných aplikací).

**V případě, že chcete přejít z podepsané verze na nepodepsanou verzi, je nutné nejprve podepsanou odinstalovat a až pak instalovat nepodepsanou verzi. Z bezpečnostních důvodů telefon nepovolí instalaci nepodepsané verze přes podepsanou verzi.**

![http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/stable/HandyGeocaching.jad&chld=L|1&choe=UTF-8&file.png](http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/stable/HandyGeocaching.jad&chld=L|1&choe=UTF-8&file.png)

### [Vývojová verze](http://code.google.com/p/handygeocaching/downloads/list?can=2&q=label:Testing) ###
Nebo pokud chcete mít ty nejnovější funkce v HG, tak [stahujte vývojovou verzi](http://code.google.com/p/handygeocaching/downloads/list?can=2&q=label:Testing). Ta je avšak **nepodepsaná**, takže vám na některé věci bude vyskakovat bezpečnostní dialog. **Také může být tato verze nestabilní.** [Novinky ve vývojové verzi](http://lab.arcao.com/aplikace/j2me/handygeocaching/vyvojova-verze.html).

_Upozornění: Pokud chcete přejít ze stabilní verze na verzi vývojovou, je nutné nejprve stabilní verzi z telefonu odstranit, a až pak instalovat vývojovou verzi. Při aktualizaci vývojové verze není nutné toto postupovat. Měla by fungovat instalace přes již nainstalovanou vývojovou verzi._

Stažení JAD, JAR souborů (instalace přímo z telefonu) poslední vývojové verze:
  * [HandyGeocaching.jad](http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/testing/HandyGeocaching.jad)
  * [HandyGeocaching.jar](http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/testing/HandyGeocaching.jar)

![http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/testing/HandyGeocaching.jad&chld=L|1&choe=UTF-8&file.png](http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=http://lab.arcao.com/aplikace/j2me/handygeocaching/latest/testing/HandyGeocaching.jad&chld=L|1&choe=UTF-8&file.png)

Nebo můžete spustit vývojovou verzi v [emulátoru](http://bluecove.org/bluecove-examples/bluecove-webstart/open/lab.arcao.com/aplikace/j2me/handygeocaching/latest/testing/HandyGeocaching.jnlp). Je možné v emulátoru připojit GPS přes Bluetooth (podpora pro Widcomm, BlueSoleil a Microsoft stack). Je možné HG [spustit i bez podpory Bluetooth](http://microemu.org/webstart/lab.arcao.com/aplikace/j2me/handygeocaching/latest/testing/HandyGeocaching.jnlp).

### [Handy Geocaching Light](http://files.arcao.com/handygeocaching/light/) ###
Naprosto osekaná verze HG, která dokáže pouze stahovat informace o keši, logovat keš, atp. Neobsahuje možnost komunikace s GPS zařízením, čímž je třeba vhodná jako doplněk k plnohodnotné GPS navigaci (např Garminu). Poslední verzi aplikace je možné nalézt na [této stránce](http://files.arcao.com/handygeocaching/light/). Aplikace funguje i s velmi starými mobilními telefony, které mají první verzi mobilní Javy (MIDP 1.0, CLDC 1.0). Zdrojáky HGL naleznete v [SVN repozitáři](http://code.google.com/p/handygeocaching/source/browse/#svn/branches/HandyGeocachingLight).

## Novinky ve verzi 3.5.2 ##
  * Nový způsoby vypisování listingů, logů, nápovědy
  * Vylepšen import GPX a nově importuje i LOC soubory (s omezením)
  * Nepadá na Nokii 5800 XM
  * Oprava hromady chyb
  * [a mnoho dalšího...](http://lab.arcao.com/aplikace/j2me/handygeocaching/historie.html)

## Autoři ##
  * [Martin "Arcao" Sloup](http://www.arcao.com/) (momentálně se stará o vývoj)
  * [David "Destil" Vávra](http://www.destil.cz) (autor původní aplikace)

---

Handy Geocaching is a mobile Java applications to facilitate geocaching. It combines mobile access to the geocaching.com site with navigation features. Allows you to search caches (the closest, according to waypoint, keyword), and download all necessary information about them (listing, help, additional waypoints, ...).

This Java ME application is localized only in Czech at the moment.
