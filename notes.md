#My notes

Override the equals, toString, and other methods the test case uses to compare.

### For ChessPiece.pieceMoves:
-Use Inheritance Abstraction  : Create PieceRule class which implements pieceMoves() and 
calls down to QueenRule subclass.
- Data table abstraction - Rule rule = switch (getPieceType()) ... using a switch statement
- Interface abstraction: Define basic movement class and add on with more classes,Concrete class implementation
    Then you have multiple classes which calculate the possible moves.
    Then you create a map using all of the classes to get the piece Rules for the desired type