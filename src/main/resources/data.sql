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
(2, 'Japón', 'Tokio', 'Una ciudad futurista con una mezcla vibrante de cultura tradicional y tecnología avanzada.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583231/tokyo-img_xnywbv.jpg', 2),
(3, 'Italia', 'Venecia', 'Canales, góndolas y una arquitectura única hacen de esta ciudad un sueño romántico.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583232/venice-img_ekobnb.jpg', 2),
(4, 'Islandia', 'Reikiavik', 'Auroras boreales, volcanes, y aguas termales en un entorno natural impresionante.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583233/iceland-img_uqzldo.jpg', 3),
(5, 'Tailandia', 'Chiang Mai', 'Templos budistas, mercados nocturnos y naturaleza tropical exuberante.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583234/chiangmai-img_crz1mv.jpg', 4),
(6, 'Estados Unidos', 'Nueva York', 'La ciudad que nunca duerme: rascacielos, Broadway y diversidad cultural.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583235/nyc-img_k6eoa7.jpg', 2),
(7, 'Australia', 'Sídney', 'Icono de la costa australiana con playas, ópera y naturaleza.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583236/sydney-img_hgjycy.jpg', 1),
(8, 'Egipto', 'El Cairo', 'Pirámides, historia milenaria y una cultura fascinante en pleno desierto.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583237/cairo-img_qzkmcz.jpg', 3),
(9, 'Francia', 'París', 'La ciudad del amor con su icónica Torre Eiffel, museos y gastronomía.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583238/paris-img_jgcsje.jpg', 4),
(10, 'Argentina', 'Bariloche', 'Paisajes de montaña, lagos y chocolate en la Patagonia argentina.', 'https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583239/bariloche-img_jsqzbg.jpg', 1);