package org.axlie.projectl.launcher_project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocRepository extends JpaRepository<Document, Long> {
}
