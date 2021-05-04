CREATE sequence curs_request_seq;
CREATE TABLE public.curs_request (
    id bigint PRIMARY KEY NOT NULL DEFAULT NEXTVAL('curs_request_seq'),
    curs_date date,
    request_date timestamp,
    correlation_id varchar,
    status varchar
    );

CREATE UNIQUE INDEX currency_request_id_uindex ON public.curs_request(id);
