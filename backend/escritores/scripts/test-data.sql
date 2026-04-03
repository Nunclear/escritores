-- Datos de Prueba para Escritores
-- Este archivo contiene datos iniciales para pruebas del sistema

-- Usuarios de Prueba
INSERT INTO app_user (login_name, email_address, password_hash, access_level, account_state, display_name, bio_text, created_at, updated_at) VALUES
('admin', 'admin@ejemplo.com', '$2a$10$dXj3SW6G7P50eKmBUeIGAOIwkQqWUKp7sxlSQzX1IzzTTNHYaO4va', 'admin', 'active', 'Administrador del Sistema', 'Soy el administrador de esta plataforma', NOW(), NOW()),
('moderador', 'moderador@ejemplo.com', '$2a$10$dXj3SW6G7P50eKmBUeIGAOIwkQqWUKp7sxlSQzX1IzzTTNHYaO4va', 'moderator', 'active', 'Moderador Principal', 'Encargado de la moderación', NOW(), NOW()),
('autor1', 'autor1@ejemplo.com', '$2a$10$dXj3SW6G7P50eKmBUeIGAOIwkQqWUKp7sxlSQzX1IzzTTNHYaO4va', 'user', 'active', 'Juan Escritor', 'Escritor apasionado de historias épicas', NOW(), NOW()),
('autor2', 'autor2@ejemplo.com', '$2a$10$dXj3SW6G7P50eKmBUeIGAOIwkQqWUKp7sxlSQzX1IzzTTNHYaO4va', 'user', 'active', 'María Novelista', 'Especialista en novelas de romance y drama', NOW(), NOW()),
('lector1', 'lector1@ejemplo.com', '$2a$10$dXj3SW6G7P50eKmBUeIGAOIwkQqWUKp7sxlSQzX1IzzTTNHYaO4va', 'user', 'active', 'Carlos Lector', 'Apasionado por la lectura', NOW(), NOW());

-- Historias de Prueba
INSERT INTO story (owner_user_id, title, slug_text, description, visibility_state, publication_state, allow_feedback, allow_scores, started_on, published_at, created_at, updated_at) VALUES
(3, 'El Viaje del Héroe', 'el-viaje-del-heroe', 'Una épica aventura en tierras desconocidas donde un joven debe encontrar su destino', 'public', 'published', 1, 1, '2024-01-15', NOW(), NOW(), NOW()),
(3, 'La Bruja del Bosque', 'la-bruja-del-bosque', 'Una historia de misterio y magia en los oscuros bosques del norte', 'public', 'published', 1, 1, '2024-02-01', NOW(), NOW(), NOW()),
(4, 'Corazones Entrelazados', 'corazones-entrelazados', 'Una novela de romance que transcurre en la época victoriana', 'private', 'draft', 0, 0, '2024-03-10', NULL, NOW(), NOW()),
(4, 'En las Sombras de la Ciudad', 'en-las-sombras-de-la-ciudad', 'Un thriller urbano con giros inesperados', 'public', 'published', 1, 1, '2024-01-20', NOW(), NOW(), NOW());

-- Arcos Narrativos
INSERT INTO arc (story_id, title, subtitle, position_index, created_at, updated_at) VALUES
(1, 'El Despertar', 'Donde todo comienza', 1, NOW(), NOW()),
(1, 'El Viaje', 'En busca de respuestas', 2, NOW(), NOW()),
(1, 'El Enfrentamiento Final', 'La batalla decisiva', 3, NOW(), NOW()),
(2, 'Las Primeras Pistas', 'Descifrando el misterio', 1, NOW(), NOW()),
(2, 'En el Corazón del Bosque', 'Verdades Ocultas', 2, NOW(), NOW()),
(4, 'El Crimen', 'El suceso que lo cambia todo', 1, NOW(), NOW());

-- Volúmenes
INSERT INTO volume (story_id, arc_id, title, position_index, created_at, updated_at) VALUES
(1, 1, 'Libro Primero', 1, NOW(), NOW()),
(1, 2, 'Libro Segundo', 2, NOW(), NOW()),
(1, 3, 'Libro Tercero', 3, NOW(), NOW()),
(2, 4, 'Volumen Uno', 1, NOW(), NOW()),
(2, 5, 'Volumen Dos', 2, NOW(), NOW()),
(4, 6, 'Parte Uno', 1, NOW(), NOW());

-- Capítulos
INSERT INTO chapter (story_id, volume_id, title, subtitle, content, published_on, position_index, reading_minutes, word_count, publication_state, created_at, updated_at) VALUES
(1, 1, 'Capítulo 1', 'El Despertar', 'En un pequeño pueblo rodeado de montañas, vivía un joven llamado Aeron. Nadie sabía que poseía un poder extraordinario que dormía en su interior. Un día, mientras caminaba por el bosque cercano, sintió un extraño hormigueo en sus manos...', '2024-01-15', 1, 5, 450, 'published', NOW(), NOW()),
(1, 1, 'Capítulo 2', 'Primeras Revelaciones', 'Los poderes de Aeron comenzaron a manifestarse de formas inesperadas. Su mentor, el sabio Elendor, le explicó la verdadera naturaleza de sus habilidades. El joven se dio cuenta de que su vida nunca volvería a ser la misma...', '2024-01-22', 2, 6, 520, 'published', NOW(), NOW()),
(1, 2, 'Capítulo 3', 'El Camino se Abre', 'Con el conocimiento de sus poderes, Aeron debe partir de su pueblo para entrenar y comprender mejor su destino. El viaje lo llevará a lugares nunca antes imaginados, donde conocerá aliados y enemigos...', '2024-02-01', 3, 7, 580, 'published', NOW(), NOW()),
(2, 4, 'Capítulo 1', 'La Noche Extraña', 'Cuando despertó, ella supo que algo terrible había sucedido en el bosque. Las luces azules que vio esa noche no eran naturales, y algo antiguo había despertado...', '2024-02-05', 1, 4, 380, 'published', NOW(), NOW()),
(4, 6, 'Capítulo 1', 'El Descubrimiento', 'El detective Marcus entra a la escena del crimen. Todo parece indicar un asesinato común, pero pronto descubre que hay mucho más detrás...', '2024-02-10', 1, 6, 500, 'published', NOW(), NOW());

-- Personajes
INSERT INTO story_character (story_id, name, description, character_role_name, profession, ability, age, birth_date, is_alive, image_url, created_at, updated_at) VALUES
(1, 'Aeron', 'Protagonista, un joven con poderes mágicos dormidos', 'Protagonista', 'Aprendiz de Mago', 'Magia Elemental', 18, '2006-05-15', 1, 'https://ejemplo.com/aeron.jpg', NOW(), NOW()),
(1, 'Elendor', 'El sabio mentor de Aeron, guardián del conocimiento antiguo', 'Mentor', 'Mago Antiguo', 'Sabiduría Ancestral', 156, '1868-03-20', 1, 'https://ejemplo.com/elendor.jpg', NOW(), NOW()),
(1, 'Kaya', 'Compañera de viaje de Aeron, guerrera valiente', 'Compañera', 'Guerrera', 'Combate Experto', 22, '2002-08-10', 1, NULL, NOW(), NOW()),
(2, 'Iara', 'La bruja del bosque, enigmática y poderosa', 'Protagonista', 'Bruja', 'Magia Oscura', 245, '1779-12-01', 1, NULL, NOW(), NOW()),
(4, 'Detective Marcus', 'Investigador perseverante', 'Protagonista', 'Detective', 'Investigación', 42, '1982-07-15', 1, NULL, NOW(), NOW());

-- Habilidades
INSERT INTO skill (story_id, name, description, category_name, level_value, created_at, updated_at) VALUES
(1, 'Lanzar Fuego', 'Capacidad de lanzar fuego controlado', 'Magia Elemental', 3, NOW(), NOW()),
(1, 'Escudo Mágico', 'Crear un escudo protector', 'Magia Defensiva', 2, NOW(), NOW()),
(1, 'Teletransportación', 'Moverse instantáneamente de un lugar a otro', 'Magia Avanzada', 4, NOW(), NOW()),
(1, 'Manejo de Espada', 'Destreza en combate con espada', 'Combate', 4, NOW(), NOW()),
(2, 'Hechizo de Oscuridad', 'Invocar la oscuridad para atacar', 'Magia Oscura', 5, NOW(), NOW()),
(2, 'Visión Nocturna', 'Ver claramente en la oscuridad', 'Magia de Percepción', 3, NOW(), NOW());

-- Asociar Habilidades a Personajes
INSERT INTO character_skill (story_character_id, skill_id, proficiency, notes, created_at, updated_at) VALUES
(1, 1, 2, 'Aeron está aprendiendo a controlar su fuego elemental', NOW(), NOW()),
(1, 2, 1, 'Recién comienza con los escudos mágicos', NOW(), NOW()),
(2, 1, 5, 'Elendor domina completamente el fuego', NOW(), NOW()),
(2, 3, 5, 'Maestro en teletransportación', NOW(), NOW()),
(3, 4, 4, 'Kaya es una guerrera experimentada', NOW(), NOW()),
(4, 5, 5, 'Iara es una experta en magia oscura', NOW(), NOW());

-- Eventos Narrativos
INSERT INTO story_event (story_id, chapter_id, title, description, event_on, importance, event_kind, created_at, updated_at) VALUES
(1, 1, 'El Despertar de Aeron', 'Aeron descubre sus primeros poderes mágicos', '2024-01-15', 5, 'Plot Point Mayor', NOW(), NOW()),
(1, 2, 'Encuentro con Elendor', 'Aeron conoce a su mentor y aprende sobre su linaje', '2024-01-22', 4, 'Plot Point Importante', NOW(), NOW()),
(1, 3, 'La Partida', 'Aeron abandona su pueblo para entrenar', '2024-02-01', 4, 'Plot Point Importante', NOW(), NOW()),
(2, 4, 'Las Luces Azules', 'Eventos sobrenaturales en el bosque', '2024-02-05', 5, 'Plot Point Mayor', NOW(), NOW());

-- Ideas Narrativas
INSERT INTO idea (story_id, title, content, created_at, updated_at) VALUES
(1, 'Giro Final Inesperado', 'Quizás el verdadero villano es alguien cercano a Aeron', NOW(), NOW()),
(1, 'Subplot Romántico', 'Desarrollar una trama romántica entre Aeron y Kaya', NOW(), NOW()),
(2, 'Pasado de Iara', 'Revelar gradualmente el oscuro pasado de la bruja', NOW(), NOW()),
(2, 'Magia Perdida', 'Buscar artefactos mágicos antiguos en el bosque', NOW(), NOW());

-- Ítems Narrativos
INSERT INTO item (story_id, name, description, quantity, unit_name, created_at, updated_at) VALUES
(1, 'Espada de Cristal', 'Una espada legendaria hecha de cristal puro', 1, 'unidad', NOW(), NOW()),
(1, 'Amuleto de Protección', 'Un amuleto que ofrece protección mágica', 5, 'unidades', NOW(), NOW()),
(1, 'Poción de Sanación', 'Poción que cura heridas', 10, 'botellas', NOW(), NOW()),
(2, 'Libro de Hechizos Antiguo', 'Un tomo con hechizos olvidados', 1, 'unidad', NOW(), NOW());

-- Comentarios
INSERT INTO story_comment (story_id, chapter_id, author_user_id, content, visibility_state, created_at, updated_at) VALUES
(1, 1, 5, '¡Qué comienzo tan emocionante! Me encantó la forma en que presentaste a Aeron.', 'visible', NOW(), NOW()),
(1, 1, 4, 'Gran trabajo, la atmósfera está muy bien construida.', 'visible', NOW(), NOW()),
(1, 2, 5, 'Elendor es un personaje fascinante, quiero saber más de su pasado.', 'visible', NOW(), NOW()),
(2, 4, 3, 'El misterio se está desarrollando muy bien, estoy intrigado.', 'visible', NOW(), NOW()),
(4, 5, 5, 'Un thriller emocionante, estoy al borde del asiento.', 'visible', NOW(), NOW());

-- Calificaciones
INSERT INTO story_rating (story_id, author_user_id, score_value, review_text, created_at, updated_at) VALUES
(1, 5, 5, 'Una historia épica y bien desarrollada. Los personajes son memorables y la trama es cautivadora.', NOW(), NOW()),
(1, 4, 4, 'Muy buena historia, aunque algunos puntos necesitan más desarrollo.', NOW(), NOW()),
(2, 5, 5, 'Misterio absoluto, excelente atmosfera. Espero ansiosamente el siguiente capítulo.', NOW(), NOW()),
(4, 3, 5, 'Un thriller magistral con giros inesperados. Recomendado 100%.', NOW(), NOW()),
(4, 5, 4, 'Buena trama, aunque algunos personajes secundarios necesitan más profundidad.', NOW(), NOW());

-- Favoritos
INSERT INTO story_favorite (user_id, story_id, created_at) VALUES
(5, 1, NOW()),
(5, 2, NOW()),
(3, 4, NOW()),
(4, 1, NOW());

-- Seguimiento de Usuarios
INSERT INTO user_follow (follower_user_id, followed_user_id, created_at) VALUES
(5, 3, NOW()),
(5, 4, NOW()),
(3, 4, NOW());

-- Avisos Globales
INSERT INTO global_notice (title, message_text, is_enabled, created_by_user_id, starts_at, ends_at, created_at, updated_at) VALUES
('¡Bienvenido a la Plataforma!', 'Gracias por ser parte de nuestra comunidad de escritores. Esperamos que disfrutes compartiendo tus historias.', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW()),
('Mantenimiento Programado', 'Se realizará un mantenimiento del servidor el próximo fin de semana. Disculpa las molestias.', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW(), NOW());

-- Logs de Visualización
INSERT INTO story_view_log (story_id, chapter_id, user_id, ip_address, viewed_at) VALUES
(1, 1, 5, '192.168.1.100', NOW()),
(1, 1, 4, '192.168.1.101', NOW()),
(1, 2, 5, '192.168.1.100', NOW()),
(2, 4, 3, '192.168.1.102', NOW()),
(4, 5, 5, '192.168.1.100', NOW());

-- Mostrar datos insertados
SELECT 'USUARIOS CREADOS' as Tipo;
SELECT id, login_name, display_name, access_level FROM app_user ORDER BY id;

SELECT '\nHISTORIAS CREADAS' as Tipo;
SELECT id, title, publication_state, visibility_state FROM story ORDER BY id;

SELECT '\nCAPÍTULOS CREADOS' as Tipo;
SELECT id, title, story_id, publication_state FROM chapter ORDER BY id;

SELECT '\nCALIFICACIONES PROMEDIO' as Tipo;
SELECT story_id, AVG(score_value) as promedio_calificacion, COUNT(*) as total_calificaciones FROM story_rating GROUP BY story_id;

SELECT '\nFAVORITOS POR USUARIO' as Tipo;
SELECT user_id, COUNT(*) as total_favoritos FROM story_favorite GROUP BY user_id;
