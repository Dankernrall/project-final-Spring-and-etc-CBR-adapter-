CREATE sequence curs_data_seq;
CREATE TABLE public.curs_data (
    id bigint PRIMARY KEY NOT NULL DEFAULT NEXTVAL('curs_data_seq'),
    currency varchar(3),
    curs numeric(5,2),
    curs_date date
    );

CREATE UNIQUE INDEX currency_id_uindex ON public.curs_data(id);
