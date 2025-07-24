INSERT INTO roles(id, role_name) VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

INSERT INTO users(id, username, email, password) VALUES
(1, 'May', 'princesitarockera@gmail.com', '$2a$12$ujrtJeyCVy992nYx8SJ8i.b0lLycVo9D5beF8/OOWj.pt1uSFpzHq'),
(2, 'Deb', 'deb@example.com', '$2a$12$mr15uTxw.QQUkbeEEO850ekrpIbTUnbuLJv9id/bnxGm4b1cHPuSO'),
(3, 'Mary', 'mary@example.com', '$2a$12$cQRHt31sbvaFOsYKMVwZy.C9mhIOCRkfcbJWg4.H/HJnlxQsU7OiC'),
(4, 'Nia', 'nia@example.com', '$2a$12$nvI3IHRGP7JorOje.D2HMub/Q/32MIsdvWtpaXgmvhy58W1Jv7AJ2'),
(5, 'Sofi', 'sofi@example.com', '$2a$12$64jtN/0uZlsqZLFof0iWOONHaJoOc0x08QuMzmP8hYwIEYPRWZtAm');

INSERT INTO users_roles(user_id, role_id) VALUES
(1, 2),
(2, 1),
(3, 1),
(4, 1),
(5, 1);

INSERT INTO destinations(id, country, city, description, image_url, user_id) VALUES
(1, 'Colombia', 'Santa Marta', 'La más hermosa y maravillosa ciudad del mundo, aunque calurosa llena de playas refrescantes', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583230/santa-marta-img_vdhss8.jpg', 1),
(2, 'Japón', 'Tokio', 'Una ciudad futurista con una mezcla vibrante de cultura tradicional y tecnología avanzada.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753360904/91683_wk3fpr.webp', 2),
(3, 'Italia', 'Venecia', 'Canales, góndolas y una arquitectura única hacen de esta ciudad un sueño romántico.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361008/b768300d-4c14-4527-8354-35eecc91c82b.png', 2),
(4, 'Islandia', 'Reikiavik', 'Auroras boreales, volcanes, y aguas termales en un entorno natural impresionante.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361105/016d71d8-bfc7-4e0e-a18e-15f9dfa724d8.png', 3),
(5, 'Tailandia', 'Chiang Mai', 'Templos budistas, mercados nocturnos y naturaleza tropical exuberante.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361214/a60733ce-f63f-442f-b02f-83b35ee45368.png', 4),
(6, 'Estados Unidos', 'Nueva York', 'La ciudad que nunca duerme: rascacielos, Broadway y diversidad cultural.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361328/74b293b8-5a29-41ae-af47-c99458e1e357.png', 2),
(7, 'Australia', 'Sídney', 'Icono de la costa australiana con playas, ópera y naturaleza.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361367/0d9be059-5ca9-437e-92cd-a766530e13d2.png', 1),
(8, 'Egipto', 'El Cairo', 'Pirámides, historia milenaria y una cultura fascinante en pleno desierto.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361398/97fd4a2e-0106-43b1-9390-b418851a650a.png', 3),
(9, 'Francia', 'París', 'La ciudad del amor con su icónica Torre Eiffel, museos y gastronomía.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361431/3e12269c-d679-4ca8-80fd-b6329d4dc083.png', 4),
(10, 'Argentina', 'Bariloche', 'Paisajes de montaña, lagos y chocolate en la Patagonia argentina.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1753361497/924ff01c-8a9d-4713-b943-95eb1fc9c9c7.png', 1);