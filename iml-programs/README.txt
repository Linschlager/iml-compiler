In diesem Ordner finden Sie jeweils ein IML-Programm (z.B. Add17.iml) und
den dazugehörigen VM-Code (z.B. Add17.vmc).
Der VM-Code ist jeweils durch meinen IML-Compiler (Haskell-Version) erzeugt
worden.

Die einzelnen Beispiele sind möglichst einfach gehalten und demonstrieren
jeweils einen bestimmten Aspekt der Kompilation.

Die Code-Erzeugung ist exakt diejenige aus dem Unterricht.
Allerdings enthalten die Instruktionen hier teilweise noch mehr Parameter
als im Unterricht besprochen, und zwar für die Behandlung unterschiedlicher
Datentypen, und für die Angabe vom Ort im Quelltext für eine informative
Beschreibung eines Laufzeitfehlers.

Sie können die Parameter mit den Orten einfach ignorieren, und für die
meisten Ihrer Projekte (aber nicht alle) reicht ein einziger Datentyp in
der VM aus, so dass Sie auch den Parameter mit dem Typ ignorieren können.

Beispiel:

Sub Int32 (6:16-22)

Der Parameter Int32 sagt, dass der Subtraktionsbefehl für den Datentyp
int32 verwendet werden soll.
Der Parameter (6:16-22) sagt, dass der entsprechende Subtraktionsausdruck
in Zeile 6, Spalten 16 bis 22 im Quelltext zu finden ist.
Bei einem Überlauf kann eine entsprechende Fehlermeldung zur Laufzeit
angezeigt werden.
(Überläufe werden in meiner VM in Haskell erkannt und mit einer
entsprechenden Fehlermeldung quittiert, in meiner VM in Java aber nicht.)

Ausserdem können Sie die AllocStack-Instruktionen ignorieren, bzw. brauchen
diese auch nicht zu erzeugen.
Sie dienen ja der Verwaltung des Extreme-Pointers, den wir gar nicht
verwenden.
