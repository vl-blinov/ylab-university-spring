SELECT
    ulab_edu.book.id AS id_b,
    ulab_edu.book.title AS title_b,
    ulab_edu.book.author AS author_b,
    ulab_edu.book.page_count AS page_count_b,
    ulab_edu.book.person_id AS person_id_b,
    ulab_edu.person.id AS id_p,
    ulab_edu.person.full_name AS full_name_p,
    ulab_edu.person.title AS title_p,
    ulab_edu.person.age AS age_p
FROM ulab_edu.book
LEFT JOIN
     ulab_edu.person ON ulab_edu.book.person_id = ulab_edu.person.id
WHERE ulab_edu.book.id = ?