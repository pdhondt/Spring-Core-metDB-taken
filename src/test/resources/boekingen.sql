insert into boekingen(naam, aantalTickets, festivalId)
values ('test1', 1, (select id from festivals where naam = 'Rock Werchter'));
insert into boekingen(naam, aantalTickets, festivalId)
values ('test2', 2, (select id from festivals where naam = 'Paradise City'));