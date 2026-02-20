package tn.iatechnology.backend.config;

import tn.iatechnology.backend.entity.HomeContent;
import tn.iatechnology.backend.repository.HomeContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initialise les clés HomeContent par défaut si elles n'existent pas.
 * Le modérateur peut ensuite modifier leurs valeurs via l'interface.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private HomeContentRepository homeContentRepository;

    @Override
    public void run(String... args) {
        initHomeContent();
    }

    private void initHomeContent() {
        List<HomeContentEntry> defaults = List.of(
                // ── HERO ──
                new HomeContentEntry("eyebrow",     "Bandeau eyebrow",       "Plateforme scientifique IA-Technology",              "TEXT", "HERO"),
                new HomeContentEntry("hero_title_1","Titre héro ligne 1",    "La recherche en IA,",                                "TEXT", "HERO"),
                new HomeContentEntry("hero_title_2","Titre héro ligne 2 (accent)", "centralisée et accessible",                   "TEXT", "HERO"),
                new HomeContentEntry("hero_desc",   "Description héro",      "Explorez, téléchargez et partagez les publications scientifiques de l'équipe IA-Technology. Filtrez par domaine, chercheur ou mots-clés.", "TEXT", "HERO"),

                // ── FEATURES ──
                new HomeContentEntry("feat_label",  "Label section fonctionnalités", "Fonctionnalités",                           "TEXT", "HERO"),
                new HomeContentEntry("feat_title",  "Titre section fonctionnalités", "Tout ce dont vous avez besoin",             "TEXT", "HERO"),
                new HomeContentEntry("feat_sub",    "Sous-titre fonctionnalités",    "Une plateforme complète pour gérer et explorer la production scientifique de l'équipe", "TEXT", "HERO"),

                // ── PUBLICATIONS ──
                new HomeContentEntry("pubs_label",  "Label section publications",    "Dernières publications",                    "TEXT", "HERO"),
                new HomeContentEntry("pubs_title",  "Titre section publications",    "Découvrez nos recherches",                  "TEXT", "HERO"),
                new HomeContentEntry("pubs_sub",    "Sous-titre publications",       "Les travaux les plus récents de l'équipe IA-Technology", "TEXT", "HERO"),

                // ── CTA ──
                new HomeContentEntry("cta_label",   "Label section CTA",            "Rejoignez la plateforme",                   "TEXT", "CTA"),
                new HomeContentEntry("cta_title",   "Titre CTA",                    "Prêt à explorer la<br/>recherche en IA ?",  "HTML", "CTA"),
                new HomeContentEntry("cta_desc",    "Description CTA",              "Créez votre compte gratuitement et accédez à toutes les publications, profils et outils de recherche.", "TEXT", "CTA"),

                // ── FOOTER ──
                new HomeContentEntry("footer_desc", "Description footer",           "Plateforme de gestion et diffusion des publications scientifiques en Intelligence Artificielle.", "TEXT", "FOOTER")
        );

        for (HomeContentEntry entry : defaults) {
            if (homeContentRepository.findByCle(entry.cle()).isEmpty()) {
                HomeContent hc = new HomeContent();
                hc.setCle(entry.cle());
                hc.setLibelle(entry.libelle());
                hc.setValeur(entry.valeur());
                hc.setType(entry.type());
                hc.setSection(entry.section());
                hc.setActif(true);
                homeContentRepository.save(hc);
            }
        }
    }

    private record HomeContentEntry(
            String cle,
            String libelle,
            String valeur,
            String type,
            String section
    ) {}
}