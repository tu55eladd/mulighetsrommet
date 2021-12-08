create table innsatsgruppe
(
    id          int primary key,
    tittel      text not null,
    beskrivelse text not null
);

alter table tiltaksvariant
    add innsatsgruppe_id int references innsatsgruppe (id) on update cascade;
