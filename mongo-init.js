db = db.getSiblingDB('blt');

db.createCollection('horse');

db.horse.insertMany([
    {"horseName":"Lightning","jockey":"Jane ","trainer":"Clark Kent","age":4,"score":68},
    {"horseName":"Flash","jockey":"John ","trainer":"Barry ","age":5,"score":70},
    {"horseName":"Bolt","jockey":"Peter","trainer":"Wally West","age":6,"score":74},
    {"horseName":"Speedy","jockey":"Brendan Sadlier","trainer":"Bart","age":4,"score":90},
    {"horseName":"Nipper","jockey":"Liam Smyth","trainer":"Jay Garrick","age":5,"score":80},
    {"horseName":"Batty","jockey":"Tommy Coogan","trainer":"Max Mercury","age":6,"score":85},
    {"horseName":"Silk","jockey":"Rem Collier","trainer":"Jesse Chambers","age":4,"score":88},
    {"horseName":"Sonic","jockey":"Cian O'Leary","trainer":"Sela Allen","age":5,"score":82},
    {"horseName":"Moonglow","jockey":"Murray","trainer":"Greg ","age":9,"score":40},
    {"horseName":"Sugarflame","jockey":"Gabriel ","trainer":"Ricky ","age":10,"score":71},
    {"horseName":"Geoffrey","jockey":"Collins","trainer":"Britton","age":3,"score":42},
    {"horseName":"Snowbolt","jockey":"Alex","trainer":"Jones","age":2,"score":17},
    {"horseName":"Vanity","jockey":"Wilde","trainer":"Daly","age":3,"score":69},
    {"horseName":"Kalina","jockey":"Patrick ","trainer":"Lloyd ","age":3,"score":58},
    {"horseName":"Charity","jockey":"Artur ","trainer":"Hunt","age":3,"score":23},
    {"horseName":"Kashmire","jockey":"Ashworth","trainer":"Williams","age":8,"score":60},
    {"horseName":"Navajo","jockey":"Jamie","trainer":"Robson","age":3,"score":63},
    {"horseName":"Bourne","jockey":"Casey","trainer":"Daryl","age":2,"score":32},
    {"horseName":"Dandi","jockey":"Drew","trainer":"Dawson","age":9,"score":46},
    {"horseName":"Flurry","jockey":"Reese","trainer":"Howarth","age":8,"score":46},
    {"horseName":"Zani","jockey":"Dickson","trainer":"Rodriguez","age":7,"score":10},
    {"horseName":"Gadget","jockey":"Cameron","trainer":"Judd","age":2,"score":29},
    {"horseName":"Cyrus","jockey":"Heywood","trainer":"Hicks","age":9,"score":84},
    {"horseName":"Colin","jockey":"Mark","trainer":"Page","age":10,"score":33},
    {"horseName":"Columbo","jockey":"Morgan","trainer":"Brown","age":2,"score":37},
    {"horseName":"Mika","jockey":"Lane","trainer":"Smith","age":5,"score":100},
    {"horseName":"Siroco","jockey":"Skyler","trainer":"Davis","age":7,"score":88},
    {"horseName":"Strider","jockey":"Taylor","trainer":"Duncan","age":6,"score":95},
    {"horseName":"Alomar","jockey":"Brewer","trainer":"Dobson","age":10,"score":94},
    {"horseName":"Sugar Sparkle","jockey":"Robbie ","trainer":"Stanley","age":5,"score":76},
    {"horseName":"Baxter","jockey":"Graves","trainer":"Fryer","age":10,"score":94},
    {"horseName":"Oddball","jockey":"Francis","trainer":"Wheatley","age":5,"score":76},
    {"horseName":"Caine","jockey":"McCann","trainer":"Whittle","age":10,"score":94},
    {"horseName":"Starman","jockey":"Whitworth","trainer":"Dutton","age":5,"score":76},
    {"horseName":"Layla","jockey":"Goldsmith","trainer":"Hood","age":10,"score":94},
    {"horseName":"Shadow","jockey":"Hutchinson","trainer":"Lamb","age":5,"score":76},
    {"horseName":"Regis","jockey":"Jordan","trainer":"Martinez","age":10,"score":94},
    {"horseName":"Scarlet","jockey":"Johnson","trainer":"Garcia","age":5,"score":76}
]);

print('Database and collection initialized, and data inserted successfully.');

db.horse.find().forEach(function(horse) {
    print(JSON.stringify(horse));
});
