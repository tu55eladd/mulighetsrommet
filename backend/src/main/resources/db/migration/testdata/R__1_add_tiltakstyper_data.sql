insert into innsatsgruppe (id, tittel, beskrivelse)
values (1, 'Standardinnsats', 'Gode muligheter'),
       (2, 'Situasjonsbestemt innsats', 'Trenger veiledning'),
       (3, 'Spesielt tilpasset innsats', 'Trenger veiledning, nedsatt arbeidsevne'),
       (4, 'Varig tilpasset innsats', 'Jobbe delvis eller liten mulighet til å jobbe')
on conflict (id) do update set tittel      = excluded.tittel,
                               beskrivelse = excluded.beskrivelse;


-- La til 50, ikke sikkert vi trenger flere.
-- insert into tiltakstype (id, innsatsgruppe_id, navn, tiltakskode, dato_fra, dato_til)
-- values  (1, 1, 'Arbeid med Bistand (AB)', 'ABIST', '2001-01-01', '2019-12-31'),
--         (2, 1, 'Arbeid med bistand A oppfølging', 'ABOPPF', '2008-08-01', '2013-12-31'),
--         (3, 1, 'Arbeid med bistand B', 'ABTBOPPF', '2008-08-01', '2013-12-31'),
--         (4, 1, 'Arbeid med bistand A utvidet oppfølging', 'ABUOPPF', '2008-08-01', '2013-12-31'),
--         (5, 1, 'AMB Avklaring (fase 1)', 'AMBF1', '2001-01-01', '2010-12-31'),
--         (6, 1, 'Kvalifisering i arbeidsmarkedsbedrift', 'AMBF2', '2001-01-01', '2099-01-01'),
--         (7, 1, 'Tilrettelagt arbeid i arbeidsmarkedsbedrift', 'AMBF3', '2001-01-01', '2019-12-31'),
--         (8, 1, 'Arbeidsmarkedsopplæring (AMO)', 'AMO', '2001-01-01', '2019-06-30'),
--         (9, 1, 'Arbeidsmarkedsopplæring (AMO) i bedrift', 'AMOB', '2009-11-20', '2015-12-31'),
--         (10, 1, 'Arbeidsmarkedsopplæring (AMO) enkeltplass', 'AMOE', '2001-01-01', '2019-06-30'),
--         (11, 1, 'Arbeidsmarkedsopplæring (AMO) yrkeshemmede', 'AMOY', '2001-01-01', '2009-01-01'),
--         (12, 1, 'Annen utdanning', 'ANNUTDANN', '2001-01-01', '2009-01-01'),
--         (13, 1, 'Arbeidsrettet rehabilitering (døgn) - sykmeldt arbeidstaker', 'ARBDOGNSM', '2012-01-01', '2019-12-31'),
--         (14, 1, 'Arbeidsforberedende trening (AFT)', 'ARBFORB', '2016-01-01', '2099-01-01'),
--         (15, 1, 'Arbeidsrettet rehabilitering (dag) - sykmeldt arbeidstaker', 'ARBRDAGSM', '2012-01-01', '2019-12-31'),
--         (16, 1, 'Arbeidsrettet rehabilitering (døgn)', 'ARBRRDOGN', '2012-01-01', '2019-12-31'),
--         (17, 1, 'Arbeidsrettet rehabilitering', 'ARBRRHBAG', '2009-01-01', '2019-12-31'),
--         (18, 1, 'Arbeidsrettet rehabilitering - sykmeldt arbeidstaker', 'ARBRRHBSM', '2009-01-01', '2019-12-31'),
--         (19, 1, 'Arbeidsrettet rehabilitering (dag)', 'ARBRRHDAG', '2012-01-01', '2099-01-01'),
--         (20, 1, 'Arbeidstrening', 'ARBTREN', '2016-01-01', '2099-01-01'),
--         (21, 1, 'Arbeidssamvirke (ASV)', 'ASV', '2001-01-01', '2010-12-31'),
--         (22, 1, 'Arbeidstreningsgrupper', 'ATG', '2001-01-01', '2010-12-31'),
--         (23, 1, 'Avklaring', 'AVKLARAG', '2009-01-01', '2099-01-01'),
--         (24, 1, 'Avklaring av kortere varighet', 'AVKLARKV', '2008-03-01', '2009-07-01'),
--         (25, 1, 'Avklaring - sykmeldt arbeidstaker', 'AVKLARSP', '2009-01-01', '2019-12-31'),
--         (26, 1, 'Avklaring i skjermet virksomhet', 'AVKLARSV', '2006-01-01', '2019-12-31'),
--         (27, 1, 'Avklaring', 'AVKLARUS', '2005-12-17', '2010-12-31'),
--         (28, 1, 'Bedriftsintern attføring', 'BIA', '2001-01-01', '2004-12-20'),
--         (29, 1, 'Bedriftsintern opplæring (BIO)', 'BIO', '2001-01-01', '2099-01-01'),
--         (30, 1, 'Brevkurs', 'BREVKURS', '2001-01-01', '2009-01-01'),
--         (31, 1, 'Digitalt oppfølgingstiltak for arbeidsledige (jobbklubb)', 'DIGIOPPARB', '2021-01-01', '2099-01-01'),
--         (32, 1, 'Diverse tiltak', 'DIVTILT', '2001-01-01', '2002-10-07'),
--         (33, 1, 'Ekspertbistand', 'EKSPEBIST', '2019-09-01', '2099-01-01'),
--         (34, 1, 'Enkeltplass AMO', 'ENKELAMO', '2019-07-01', '2099-01-01'),
--         (35, 1, 'Enkeltplass Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning', 'ENKFAGYRKE', '2019-07-01', '2099-01-01'),
--         (36, 1, 'Egenetablering', 'ETAB', '2001-01-01', '2099-01-01'),
--         (37, 1, 'Fleksibel jobb - lønnstilskudd av lengre varighet', 'FLEKSJOBB', '2001-01-01', '2005-12-31'),
--         (38, 1, 'Forsøk AMO enkeltplass', 'FORSAMOENK', '2020-01-01', '2099-01-01'),
--         (39, 1, 'Forsøk AMO gruppe', 'FORSAMOGRU', '2020-01-01', '2099-01-01'),
--         (40, 1, 'Forsøk fag- og yrkesopplæring enkeltplass', 'FORSFAGENK', '2020-01-01', '2099-01-01'),
--         (41, 1, 'Forsøk fag- og yrkesopplæring gruppe', 'FORSFAGGRU', '2020-01-01', '2099-01-01'),
--         (42, 1, 'Forsøk høyere utdanning', 'FORSHOYUTD', '2020-01-01', '2099-01-01'),
--         (43, 1, 'Funksjonsassistanse', 'FUNKSJASS', '2012-01-01', '2099-01-01'),
--         (44, 1, 'Gruppe Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning', 'GRUFAGYRKE', '2019-07-01', '2099-01-01'),
--         (45, 1, 'Grunnskole', 'GRUNNSKOLE', '2001-01-01', '2009-01-01'),
--         (46, 1, 'Gruppe AMO', 'GRUPPEAMO', '2019-07-01', '2099-01-01'),
--         (47, 1, 'Høyere utdanning', 'HOYEREUTD', '2019-07-01', '2099-01-01'),
--         (48, 1, 'Høyskole/Universitet', 'HOYSKOLE', '2001-01-01', '2009-01-01'),
--         (49, 1, 'Individuell jobbstøtte (IPS)', 'INDJOBSTOT', '2018-03-17', '2099-01-01'),
--         (50, 1, 'Oppfølging', 'INDOPPFAG', '2009-01-01', '2099-01-01')
--         ('Individuelt oppfølgingstiltak', 'INDOPPFOLG', '2008-03-01', '2009-07-01'),
--         ('Oppfølging - sykmeldt arbeidstaker', 'INDOPPFSP', '2009-01-01', '2019-12-31'),
--         ('Resultatbasert finansiering av formidlingsbistand', 'INDOPPRF', '2013-07-01', '2099-01-01'),
--         ('Inkluderingstilskudd', 'INKLUTILS', '2016-01-01', '2099-01-01'),
--         ('Nye plasser institusjonelle tiltak', 'INST_S', '2001-01-01', '2009-01-01'),
--         ('Individuell karrierestøtte (IPS Ung)', 'IPSUNG', '2021-04-01', '2099-01-01'),
--         ('Integreringstilskudd', 'ITGRTILS', '2001-01-01', '2010-12-31'),
--         ('Jobbklubb med bonusordning', 'JOBBBONUS', '2003-10-10', '2010-12-31'),
--         ('Jobbfokus/Utvidet formidlingsbistand', 'JOBBFOKUS', '2005-01-01', '2013-12-31'),
--         ('Jobbklubb', 'JOBBK', '2003-10-10', '2099-01-01'),
--         ('Intern jobbklubb', 'JOBBKLUBB', '2001-01-01', '2013-12-31'),
--         ('Jobbskapingsprosjekter', 'JOBBSKAP', '2001-01-01', '2015-12-31'),
--         ('Formidlingstjenester', 'KAT', '2002-12-01', '2005-12-31'),
--         ('Andre kurs', 'KURS', '2001-01-01', '2009-01-01'),
--         ('Tidsbegrenset lønnstilskudd', 'LONNTIL', '2009-01-01', '2019-12-31'),
--         ('Arbeidsavklaringspenger som lønnstilskudd', 'LONNTILAAP', '2013-01-01', '2099-01-01'),
--         ('Lønnstilskudd av lengre varighet', 'LONNTILL', '2005-12-18', '2010-12-31'),
--         ('Lønnstilskudd', 'LONNTILS', '2001-01-01', '2010-12-31'),
--         ('Mentor', 'MENTOR', '2012-01-01', '2099-01-01'),
--         ('Midlertidig lønnstilskudd', 'MIDLONTIL', '2016-01-01', '2099-01-01'),
--         ('Nettbasert arbeidsmarkedsopplæring (AMO)', 'NETTAMO', '2005-01-01', '2019-06-30'),
--         ('Nettkurs', 'NETTKURS', '2001-01-01', '2009-01-01'),
--         ('2-årig opplæringstiltak', 'OPPLT2AAR', '2016-01-01', '2019-06-30'),
--         ('Arbeidspraksis i skjermet virksomhet', 'PRAKSKJERM', '2001-01-01', '2019-12-31'),
--         ('Arbeidspraksis i ordinær virksomhet', 'PRAKSORD', '2001-01-01', '2019-12-31'),
--         ('Produksjonsverksted (PV)', 'PV', '2001-01-01', '2099-01-01'),
--         ('Lønnstilskudd - reaktivisering av uførepensjonister', 'REAKTUFOR', '2001-01-01', '2009-01-01'),
--         ('Resultatbasert finansiering av oppfølging', 'REFINO', '2018-03-17', '2099-01-01'),
--         ('Spa prosjekter', 'SPA', '2001-01-01', '2010-12-31'),
--         ('Lærlinger i statlige etater', 'STATLAERL', '2001-01-01', '2002-09-12'),
--         ('Supported Employment', 'SUPPEMP', '2014-01-01', '2099-01-01'),
--         ('Sysselsettingstiltak for langtidsledige', 'SYSSLANG', '2001-01-01', '2009-01-01'),
--         ('Sysselsettingstiltak i offentlig sektor for yrkeshemmede', 'SYSSOFF', '2001-01-01', '2015-12-31'),
--         ('Tidsubestemt lønnstilskudd', 'TIDSUBLONN', '2007-04-01', '2099-01-01'),
--         ('Tilretteleggingstilskudd for arbeidssøker', 'TILPERBED', '2012-01-01', '2099-01-01'),
--         ('Tilrettelegging for arbeidstaker', 'TILRETTEL', '2003-07-13', '2004-12-31'),
--         ('Forebyggings- og tilretteleggingstilskudd IA virksomheter og BHT-honorar', 'TILRTILSK', '2009-01-01', '2099-01-01'),
--         ('Tilskudd til sommerjobb', 'TILSJOBB', '2021-04-01', '2021-08-31'),
--         ('Uførepensjon som lønnstilskudd', 'UFØREPENLØ', '2005-01-01', '2009-01-01'),
--         ('Utredning/behandling lettere psykiske lidelser', 'UTBHLETTPS', '2012-01-01', '2019-12-31'),
--         ('Utredning/behandling lettere psykiske og sammensatte lidelser', 'UTBHPSLD', '2009-01-01', '2019-12-31'),
--         ('Utredning/behandling sammensatte lidelser', 'UTBHSAMLI', '2012-01-01', '2019-12-31'),
--         ('Utdanningspermisjoner', 'UTDPERMVIK', '2007-09-01', '2010-12-31'),
--         ('Utdanning', 'UTDYRK', '2001-01-01', '2019-06-30'),
--         ('Utvidet oppfølging i NAV', 'UTVAOONAV', '2013-12-09', '2099-01-01'),
--         ('Utvidet oppfølging i opplæring', 'UTVOPPFOPL', '2021-03-19', '2099-01-01'),
--         ('Formidlingstjenester - Ventelønn', 'VALS', '2003-01-01', '2005-12-31'),
--         ('Varig lønnstilskudd', 'VARLONTIL', '2016-01-01', '2099-01-01'),
--         ('Varig tilrettelagt arbeid i skjermet virksomhet', 'VASV', '2001-01-01', '2099-01-01'),
--         ('Varig tilrettelagt arbeid i ordinær virksomhet', 'VATIAROR', '2006-01-01', '2099-01-01'),
--         ('Videregående skole', 'VIDRSKOLE', '2001-01-01', '2009-01-01'),
--         ('Utdanningsvikariater', 'VIKARBLED', '2001-01-01', '2010-12-31'),
--         ('Varig vernet arbeid (VVA)', 'VV', '2001-01-01', '2099-01-01'),
--         ('Sysselsettingstiltak for yrkeshemmede', 'YHEMMOFF', '2001-01-01', '2015-12-31')
-- ON CONFLICT (id) DO UPDATE SET innsatsgruppe_id = EXCLUDED.innsatsgruppe_id,
--                                navn             = EXCLUDED.navn;
--
-- SELECT setval('tiltakstype_id_seq', (SELECT MAX(id) from "tiltakstype"));
