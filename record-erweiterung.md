# IML-Erweiterung: Records

## Code-Beispiel

```
program recordsDemo()
global
 
  record point3D(x:int64, y:int64, z:int64);
  record point2D(x:int64, y:int64);
 
  record vector2D(p1:point2D, p2:point2D);
 
  const p1 : point2D;
  var   p2 : point3D;
  const vec: vector2D
 
do
  p1 init := point2D(5, -4);
  p2 init := point3D(p1.x, p1.y, 0);
  p2.z    := p2.z + 10;
 
  vec init := vector2D(p1, point2D(0, 0))
 
endprogram
```

## Beschreibung

- Records können deklariert werden wo auch variablen / funktionen / procedures deklariert werden können
- Sie können eine beliebige Menge an Feldern haben (mindestens 1)
- Alle Felder haben einen Namen (gleiche Regeln wie bei sonstigen Identifiern)
- Alle Datentypen und zuvor deklarierte Records können als Typ der Felder verwendet werden
- Records können const oder var sein (wie oben p1 / p2 im code):
    - const: nur Initialisierung möglich, keine updates der Felder
    - var: Initialisierung & Updates möglich
    - Reads sind in beiden Fällen nach Initialisierung beliebig möglich
- Initialisiert werden Records mit ihrem Namen und der Felder in runden Klammern (in der gleichen Reihenfolge wie sie in der Record-Deklaration sind)
    - Alternativ könnten wir uns auch vorstellen das man den Namen des Records auch weglassen kann und/oder dass Felder auch mit explizitem Namen initialisiert werden können:  
    `p2 init := { x := p1.x, z := 0, y := p1.y }; // oder so etwas in dieser Richtung `
- Zugegriffen wird auf die Felder mit Punktnotation
- Initialisierung/Zuweisungen auf Record Felder sind immer Kopien des Werts der Expression

## Lexikografische Erweiterungen zu IML

TODO (neues keyword record und neues symbol "."🤔)

## Grammatikalische Erweiterungen zu IML
### Deklarationen
```
<typedIdents> ::= <typedIdent> [',' <typedIdents>]
<recordShapeDecl> ::= record <ident> '(' <typedIdents> ')'

<type> ::= bool
         | <inttype>
         | <ident> // <- Für record-typen

<decl> ::= <stoDecl>
         | <funDecl>
         | <procDecl>
         | <recordShapeDecl> // <-
```
### Init and Usage
```
<factor> ::= <literal>
           | <ident> [init | <exprList>]
           | <monopr> <factor>
           | <recordExpr> // <-
           | '(' <expr> ')'

<recordExpr> ::= <ident> {'.' <recordExpr>}
<recordConstructDecl> ::= <ident> <exprList> // recordName(whatever,how,many,elements)
<cmd> ::= skip
        | <expr> := (<expr> | <recordConstructDecl>) // <-
        | if <expr> then <cpsCmd>
         [else <cpsCmd>] endif
        | while <expr> do <cpsCmd> endwhile
        | call <ident> <exprList> [<globInits>]
        | debugin <expr>
        | debugout <expr>
```
