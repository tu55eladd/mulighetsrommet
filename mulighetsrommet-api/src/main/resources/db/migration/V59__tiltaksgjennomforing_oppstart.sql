create type tiltaksgjennomforing_oppstartstype as enum ('Felles', 'Lopende');

alter table tiltaksgjennomforing
    add oppstart tiltaksgjennomforing_oppstartstype;

update tiltaksgjennomforing tg
set oppstart = case
                   when tt.tiltakskode in ('GRUPPEAMO', 'JOBBK', 'GRUFAGYRKE') then 'Felles'
                   else 'Lopende' end::tiltaksgjennomforing_oppstartstype
from tiltakstype tt
where tt.id = tg.tiltakstype_id;

alter table tiltaksgjennomforing
    alter oppstart set not null;
