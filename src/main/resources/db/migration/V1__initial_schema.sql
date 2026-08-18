create table accomodation
(
    number_of_client              integer not null,
    id                            uuid    not null,
    category                      varchar(255) check (category in ('GENERAL', 'HOTEL', 'DORMITORY', 'GUESTHOUSE', 'AIRBNB')),
    checkin_date_iso_string       varchar(255),
    checkin_end_time_iso_string   varchar(255),
    checkin_start_time_iso_string varchar(255),
    checkout_date_iso_string      varchar(255),
    checkout_time_iso_string      varchar(255),
    client_name                   varchar(255),
    location                      varchar(255),
    room_title                    varchar(255),
    title                         varchar(255),
    links                         jsonb,
    primary key (id)
);
create table airline
(
    iata_code varchar(255),
    icao_code varchar(255) not null,
    title     varchar(255),
    primary key (icao_code)
);
create table airport
(
    airport_name          varchar(255),
    city_name             varchar(255),
    iata_code             varchar(255) not null,
    iso2digit_nation_code varchar(255),
    primary key (iata_code)
);
create table custom_todo_content
(
    flight_todo_content_id uuid unique,
    id                     uuid not null,
    category               varchar(255) check (category in
                                               ('WORK', 'RESERVATION', 'FOREIGN', 'SUPPLY', 'WASH', 'ELECTRONICS',
                                                'CLOTHING')),
    subtitle               varchar(255),
    title                  varchar(255),
    type                   varchar(255),
    icon                   jsonb,
    primary key (id)
);
create table destination
(
    id                    uuid not null,
    description           varchar(255),
    iso2digit_nation_code varchar(255),
    region                varchar(255),
    title                 varchar(255),
    primary key (id),
    constraint uk_destination_title_code_region unique (title, iso2digit_nation_code, region)
);
create table destination_outbound
(
    destination_id    uuid not null,
    "flight-route_id" uuid not null
);
create table destination_return
(
    destination_id    uuid not null,
    "flight-route_id" uuid not null
);
create table "flight-todo-content_flight-route"
(
    "flight-route_id"        uuid not null,
    "flight-todo-content_id" uuid not null
);
create table flight_booking
(
    number_of_passenger            integer not null,
    id                             uuid    not null,
    todo_id                        uuid unique,
    arrival_airport_iata_code      varchar(255),
    departure_airport_iata_code    varchar(255),
    departure_date_time_iso_string varchar(255),
    flight_number                  varchar(255),
    passenger_name                 varchar(255),
    primary key (id)
);
create table flight_route
(
    id                   uuid not null,
    arrival_airport_id   varchar(255),
    departure_airport_id varchar(255),
    primary key (id)
);
create table flight_route_airlines
(
    flight_route_id    uuid         not null,
    airlines_icao_code varchar(255) not null unique
);
create table flight_ticket
(
    id                             uuid not null,
    todo_id                        uuid unique,
    arrival_airport_iata_code      varchar(255),
    departure_airport_iata_code    varchar(255),
    departure_date_time_iso_string varchar(255),
    flight_number                  varchar(255),
    passenger_name                 varchar(255),
    primary key (id)
);
create table flight_todo_content
(
    id uuid not null,
    primary key (id)
);
create table general_reservation
(
    number_of_client     integer not null,
    id                   uuid    not null,
    client_name          varchar(255),
    date_time_iso_string varchar(255),
    title                varchar(255),
    primary key (id)
);
create table reservation
(
    is_completed           boolean,
    accomodation_id        uuid unique,
    flight_booking_id      uuid unique,
    flight_ticket_id       uuid unique,
    general_reservation_id uuid unique,
    id                     uuid not null,
    trip_id                uuid,
    visit_japan_id         uuid unique,
    primary_href_link      varchar(2048),
    category               varchar(255) check (category in
                                               ('UNKNOWN', 'GENERAL', 'FLIGHT_BOOKING', 'FLIGHT_TICKET', 'ACCOMODATION',
                                                'VISIT_JAPAN')),
    code                   varchar(255),
    note                   varchar(255),
    raw_text               oid,
    primary key (id)
);
create table stock_todo_content
(
    id       uuid default gen_random_uuid() not null,
    todo_id  uuid,
    category varchar(255) check (category in
                                 ('WORK', 'RESERVATION', 'FOREIGN', 'SUPPLY', 'WASH', 'ELECTRONICS', 'CLOTHING')),
    subtitle varchar(255),
    title    varchar(255),
    type     varchar(255) unique,
    icon     jsonb,
    primary key (id)
);
create table todo
(
    order_key                integer not null,
    custom_todo_content_id   uuid unique,
    id                       uuid    not null,
    "preset-todo-content_id" uuid,
    trip_id                  uuid,
    complete_date_iso_string varchar(255),
    note                     varchar(255),
    primary key (id)
);
create table todo_preset
(
    id    uuid not null,
    title varchar(255),
    type  varchar(255) unique check (type in ('DEFAULT', 'DOMESTIC', 'DOMESTIC_FLIGHT', 'FOREIGN', 'JAPAN')),
    primary key (id)
);
create table todo_preset_stock_todo_content
(
    is_flagged_to_add       boolean,
    order_key               integer not null,
    "stock-todo-content_id" uuid    not null,
    "todo-preset_id"        uuid    not null,
    primary key ("stock-todo-content_id", "todo-preset_id")
);
create table trip
(
    is_initialized         boolean,
    is_sample              boolean,
    is_todo_preset_updated boolean,
    id                     uuid not null,
    settings_id            uuid unique,
    todo_preset_id         uuid,
    user_account_id        uuid,
    create_date_iso_string varchar(255),
    end_date_iso_string    varchar(255),
    start_date_iso_string  varchar(255),
    title                  varchar(255),
    primary key (id)
);
create table trip_destination
(
    destination_id uuid not null,
    trip_id        uuid not null,
    primary key (destination_id, trip_id)
);
create table trip_settings
(
    is_trip_mode          boolean,
    id                    uuid not null,
    category_key_to_index jsonb,
    primary key (id)
);
create table user_account
(
    active_trip_id uuid,
    id             uuid not null,
    google_id      varchar(255),
    kakao_id       varchar(255),
    nickname       varchar(255),
    user_role      varchar(255) check (user_role in ('ADMIN', 'USER')),
    google_profile jsonb,
    kakao_profile  jsonb,
    primary key (id)
);
create table visit_japan
(
    id                   uuid not null,
    date_time_iso_string varchar(255),
    primary key (id)
);
create index idx_sample on trip (is_sample);
alter table if exists custom_todo_content add constraint FK4866gpr55o50uel7xwpkaj682 foreign key (flight_todo_content_id) references flight_todo_content;
alter table if exists destination_outbound add constraint FKktbw0b64utji392frk20m3b5y foreign key ("flight-route_id") references flight_route;
alter table if exists destination_outbound add constraint FK98f7tn8hksirwt7w9sgp6cka1 foreign key (destination_id) references destination;
alter table if exists destination_return add constraint FK6pbg469gbr224ypr3aln0ohhj foreign key ("flight-route_id") references flight_route;
alter table if exists destination_return add constraint FKsjf03bxauus5u4uk137gdmsgo foreign key (destination_id) references destination;
alter table if exists "flight-todo-content_flight-route" add constraint FKq9gkv36n5kf9lf3np2f7flnhq foreign key ("flight-route_id") references flight_route;
alter table if exists "flight-todo-content_flight-route" add constraint FKf7fr97c8qn4viw65lbeglojss foreign key ("flight-todo-content_id") references flight_todo_content;
alter table if exists flight_booking add constraint FKarn2psoshjhx378ykihfejvet foreign key (arrival_airport_iata_code) references airport;
alter table if exists flight_booking add constraint FKfsenpxdadp3jbpc4y2ahyd71x foreign key (departure_airport_iata_code) references airport;
alter table if exists flight_booking add constraint FKqgf6wktf3d9vv37jricgyqsw3 foreign key (todo_id) references todo;
alter table if exists flight_route add constraint FKst72afvtyxqciwubyw5m3uhix foreign key (arrival_airport_id) references airport;
alter table if exists flight_route add constraint FK3mbq55v3k4k9794guepnmgngq foreign key (departure_airport_id) references airport;
alter table if exists flight_route_airlines add constraint FK7amogiwyun8s04yskv1bucpsa foreign key (airlines_icao_code) references airline;
alter table if exists flight_route_airlines add constraint FKk5sru5doa4r8jby3twp84l2ix foreign key (flight_route_id) references flight_route;
alter table if exists flight_ticket add constraint FKalb1vks0v7p571ei1b6bikxa5 foreign key (arrival_airport_iata_code) references airport;
alter table if exists flight_ticket add constraint FK5t61s0s7hsdtpuu3pvdd9uimv foreign key (departure_airport_iata_code) references airport;
alter table if exists flight_ticket add constraint FK75dccc8i16skxtbnamxpmfp7u foreign key (todo_id) references todo;
alter table if exists reservation add constraint FKhgm7mmkdvvwsyv30yt5s5lli2 foreign key (accomodation_id) references accomodation on delete cascade;
alter table if exists reservation add constraint FKmd7wejh5crgp8v7jwfqgoo3m7 foreign key (flight_booking_id) references flight_booking on delete cascade;
alter table if exists reservation add constraint FKcf1wp4l95xeb90490q3mdswr1 foreign key (flight_ticket_id) references flight_ticket on delete cascade;
alter table if exists reservation add constraint FK66lf7jx9hgcpqegby871tfebw foreign key (general_reservation_id) references general_reservation on delete cascade;
alter table if exists reservation add constraint FKbnq7qaveg8wkoee526abhsykn foreign key (trip_id) references trip on delete cascade;
alter table if exists reservation add constraint FKnp9n7mlvpvaacg42cx4pd85xd foreign key (visit_japan_id) references visit_japan on delete cascade;
alter table if exists stock_todo_content add constraint FKag6h2b8ntjlgnq7kku3t4qfqa foreign key (todo_id) references todo;
alter table if exists todo add constraint FK34qcpgbavlbcgdwdh3asxwt2u foreign key (custom_todo_content_id) references custom_todo_content on delete cascade;
alter table if exists todo add constraint FKj2j0eej8ty8lup5jeijd0ti9c foreign key ("preset-todo-content_id") references stock_todo_content;
alter table if exists todo add constraint FKoorqutk68baww77wa379y4lpu foreign key (trip_id) references trip on delete cascade;
alter table if exists todo_preset_stock_todo_content add constraint FK2nggw14vq811sl1984p3q0nfu foreign key ("stock-todo-content_id") references stock_todo_content;
alter table if exists todo_preset_stock_todo_content add constraint FKp9ctr2i5cf2nj3gdkpjjbxwwu foreign key ("todo-preset_id") references todo_preset;
alter table if exists trip add constraint FKd62j0qf4hw336ixn0rjdsmt0o foreign key (settings_id) references trip_settings on delete cascade;
alter table if exists trip add constraint FKgtv74q9qmt59np3aa6kurjeq9 foreign key (todo_preset_id) references todo_preset;
alter table if exists trip add constraint FKgrch0674id726xjwsvrwqb6y1 foreign key (user_account_id) references user_account on delete cascade;
alter table if exists trip_destination add constraint FKs96sy5jry5b9eadclictvcmpe foreign key (destination_id) references destination on delete cascade;
alter table if exists trip_destination add constraint FKfxysi4rhydcnr09btlul6f74j foreign key (trip_id) references trip on delete cascade;
