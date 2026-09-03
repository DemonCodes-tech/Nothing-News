package com.example.data.sample

import com.example.data.model.NewsArticle

object CuratedDispatches {
    fun getInitialArticles(): List<NewsArticle> {
        val now = System.currentTimeMillis()
        val hour = 3600 * 1000L
        val day = 24 * hour

        return listOf(
            NewsArticle(
                id = "pal_001",
                title = "UN OCHA Issues Critical Assessment on Gaza Clean Water and Healthcare Access",
                summary = "United Nations relief agencies report severe constraints on fuel supplies for water desalination plants and neonatal hospital wards across central and southern Gaza.",
                fullContent = """GENEVA / JERUSALEM — The UN Office for the Coordination of Humanitarian Affairs (OCHA), alongside the World Health Organization (WHO) and UNICEF, has released an urgent bulletin detailing the escalating emergency in Gaza's critical infrastructure.

According to the latest UN field assessment:
• Only 14 out of 36 hospitals in Gaza remain partially functional, operating at over 250% bed capacity with severe shortages of anesthesia, antibiotics, and surgical supplies.
• Desalination facilities in Deir al-Balah and Khan Younis have seen electricity access cut to less than 4 hours daily, reducing potable water production by 70%.
• Over 1.9 million civilians remain internally displaced, with shelter conditions exacerbated by extreme heat and damaged sanitation networks.

UN High Commissioner for Human Rights Volker Türk reiterated calls for unhindered humanitarian access and the immediate protection of all humanitarian personnel and healthcare facilities under international humanitarian law.""",
                source = "UN OCHA / WHO",
                url = "https://www.ochaopt.org",
                imageUrl = "https://images.unsplash.com/photo-1541881682708-30687747e9b8?auto=format&fit=crop&q=80&w=800",
                publishedAt = "25.08.2026 // 08:45 GMT",
                timestamp = now - (15 * 60 * 1000L),
                category = "HUMANITARIAN",
                isPalestine = true,
                isBreaking = true,
                isLive = true,
                location = "GAZA STRIP // UN GENEVA",
                keyTakeaways = "14/36 hospitals partially active, Water output down 70%, 1.9M displaced, Immediate fuel corridors demanded",
                unReportReference = "UN-OCHA Flash Update #248"
            ),
            NewsArticle(
                id = "pal_002",
                title = "ICJ Advisory Opinion: International Legal Obligations Regarding Palestinian Territory",
                summary = "The International Court of Justice in The Hague reviews international compliance and state obligations concerning the occupied West Bank, East Jerusalem, and the Gaza Strip.",
                fullContent = """THE HAGUE — The International Court of Justice (ICJ) held subsequent deliberations following its historic Advisory Opinion regarding legal consequences arising from policies and practices in the Occupied Palestinian Territory.

Key legal findings reaffirmed by the bench:
1. The Court determined that continued presence in the occupied territory is contrary to the right of self-determination and the principle of non-acquisition of territory by force.
2. Member states are obligated under international law not to render aid or assistance in maintaining the status quo contrary to international conventions.
3. The Fourth Geneva Convention remains fully applicable to all civilians in occupied territories, including East Jerusalem and the West Bank.

Diplomatic delegations from over 45 countries submitted follow-up memoranda emphasizing adherence to international judicial mechanisms.""",
                source = "ICJ / REUTERS",
                url = "https://www.icj-cij.org",
                imageUrl = "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=800",
                publishedAt = "25.08.2026 // 07:30 GMT",
                timestamp = now - (1 * hour),
                category = "DIPLOMACY",
                isPalestine = true,
                isBreaking = false,
                isLive = false,
                location = "THE HAGUE // NETHERLANDS",
                keyTakeaways = "Advisory opinion on state obligations, Self-determination affirmed under 4th Geneva Convention, 45 nations filed follow-up briefs",
                unReportReference = "ICJ Advisory Opinion General List No. 186"
            ),
            NewsArticle(
                id = "pal_003",
                title = "West Bank: UNRWA Reports Spike in Infrastructure Damage Across Jenin and Tulkarm",
                summary = "Road networks, water mains, and electricity grids in northern West Bank refugee camps face extensive disruptions following intensified security operations.",
                fullContent = """RAMALLAH / AMMAN — UNRWA Commissioner-General Philippe Lazzarini issued a statement drawing global attention to the acute deterioration of public infrastructure in refugee camps in the northern West Bank, including Jenin, Nur Shams, and Tulkarm.

According to UNRWA field teams:
• Over 32 kilometers of water and sewage piping were damaged or severed, cutting basic utilities to approximately 40,000 residents.
• UNRWA schools and primary health clinics have had to transition to emergency remote schedules due to road blockades and safety hazards.
• Palestinian trade and daily transit between Nablus, Ramallah, and Hebron face severe checkpoint delays averaging 4 to 6 hours.

Lazzarini emphasized: 'The economic fabric of the West Bank is under unprecedented strain. Unrestricted civilian movement and basic service rehabilitation must be restored immediately.'""",
                source = "UNRWA / WAFA",
                url = "https://www.unrwa.org",
                imageUrl = "https://images.unsplash.com/photo-1601581458928-8fc79c46ce9a?auto=format&fit=crop&q=80&w=800",
                publishedAt = "25.08.2026 // 06:15 GMT",
                timestamp = now - (2 * hour),
                category = "HUMANITARIAN",
                isPalestine = true,
                isBreaking = false,
                isLive = true,
                location = "WEST BANK // RAMALLAH",
                keyTakeaways = "32km utility networks severed in Jenin & Tulkarm, 40,000 residents affected, Checkpoint delays up to 6 hours",
                unReportReference = "UNRWA Situation Report #112"
            ),
            NewsArticle(
                id = "world_001",
                title = "Global Summit: UN Security Council Convenes Emergency Session on Middle East De-escalation",
                summary = "Diplomats from 15 member states debate binding framework for immediate ceasefire, captive exchanges, and unimpeded humanitarian convoys.",
                fullContent = """NEW YORK — The United Nations Security Council met at UN Headquarters in New York for high-level consultations on securing a comprehensive regional peace architecture.

Discussions focused on:
• Transitioning temporary halts into durable, verified cessation of hostilities.
• Guaranteeing daily minimums of 500 aid trucks entering through all designated border crossings including Rafah, Kerem Shalom, and northern maritime corridors.
• Establishing an independent UN monitoring mechanism to ensure aid reaches civilian distribution centers directly.

Representatives from regional mediator states Qatar and Egypt briefed the council on ongoing multi-party talks held in Cairo and Doha.""",
                source = "UN NEWS / AP",
                url = "https://news.un.org",
                imageUrl = "https://images.unsplash.com/photo-1572004245607-009f4e24ebce?auto=format&fit=crop&q=80&w=800",
                publishedAt = "25.08.2026 // 05:00 GMT",
                timestamp = now - (3 * hour),
                category = "WORLD",
                isPalestine = false,
                isBreaking = true,
                isLive = true,
                location = "UN HQ // NEW YORK",
                keyTakeaways = "15-member council debates binding framework, 500 truck/day aid quota proposed, UN verification mechanism under review",
                unReportReference = "UNSC Draft Res / S-2026/89"
            ),
            NewsArticle(
                id = "pal_004",
                title = "Red Cross & Red Crescent Movement: Surge in Malnutrition Cases Among Children in Northern Gaza",
                summary = "Field nutritional screenings reveal critical acute malnutrition rates among children under five as essential food supply corridors remain restricted.",
                fullContent = """JERUSALEM / GENEVA — The International Committee of the Red Cross (ICRC) and the Palestinian Red Crescent Society (PRCS) have sounded the alarm over catastrophic nutrition indicators in northern Gaza governorates.

Key findings from joint clinical surveys:
• Over 28% of infants screened in makeshift clinics exhibit moderate-to-severe acute malnutrition.
• Essential therapeutic milk (F-75/F-100) and high-energy nutrient pastes have depleted across public dispensaries.
• PRCS mobile medical teams report severe obstacles navigating unpaved rubble pathways to deliver emergency medical food.

The ICRC called on all parties to facilitate immediate, unhindered passages for specialized pediatric medical relief convoys.""",
                source = "ICRC / AL JAZEERA",
                url = "https://www.icrc.org",
                imageUrl = "https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?auto=format&fit=crop&q=80&w=800",
                publishedAt = "24.08.2026 // 22:10 GMT",
                timestamp = now - (10 * hour),
                category = "HUMANITARIAN",
                isPalestine = true,
                isBreaking = false,
                isLive = false,
                location = "NORTHERN GAZA // GENEVA",
                keyTakeaways = "28% of screened infants acutely malnourished, Therapeutic milk stocks exhausted, Emergency pediatric corridors urged",
                unReportReference = "IPC Acute Food Insecurity Phase 5 Alert"
            ),
            NewsArticle(
                id = "world_002",
                title = "G7 Foreign Ministers Announce Multilateral Humanitarian Trust Fund for Regional Recovery",
                summary = "Ministers pledge $1.8 billion towards long-term reconstruction, medical evacuations, and water sanitation rebuilding once durable security is achieved.",
                fullContent = """BRUSSELS / TOKYO — Foreign ministers representing the Group of Seven (G7) nations concluded a ministerial summit in Brussels by outlining a joint pledge for regional post-conflict rehabilitation.

The framework commitments include:
• $1.8 Billion initial allocation for critical utility rebuilding (electricity grids, solar microgrids, and water desalination).
• Establishment of international medical evacuation corridors to specialized pediatric burn and orthopedic centers in Europe and the Middle East.
• Direct grants to support Palestinian civil society and educational institution revival.

The communique noted that sustainable reconstruction is inextricably tied to a viable two-state framework and guaranteed regional security.""",
                source = "REUTERS / BBC",
                url = "https://www.reuters.com",
                imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&q=80&w=800",
                publishedAt = "24.08.2026 // 18:30 GMT",
                timestamp = now - (14 * hour),
                category = "WORLD",
                isPalestine = false,
                isBreaking = false,
                isLive = false,
                location = "BRUSSELS // BELGIUM",
                keyTakeaways = "$1.8B multilateral reconstruction pledge, Medical evacuation corridors established, Two-state political path affirmed",
                unReportReference = "G7 Foreign Ministers Communique 2026"
            ),
            NewsArticle(
                id = "pal_005",
                title = "Gaza Healthcare Infrastructure: Field Hospitals Struggle Amid Generators Diesel Deficit",
                summary = "Doctors Without Borders (MSF) and Medical Aid for Palestinians report surgical theaters operating on emergency battery backups.",
                fullContent = """KHAN YOUNIS / LONDON — Médecins Sans Frontières (MSF) has warned that surgical interventions and intensive care units at the Nasser Medical Complex and European Gaza Hospital are at immediate risk of shutdown due to a severe diesel shortage.

Details from field coordinators:
• Essential backup generators are rationed to life-support systems only; air conditioning, sterilization units, and oxygen concentrators are operating intermittently.
• Over 400 trauma patients requiring emergency orthopedic surgeries are on waiting lists in makeshift ward tents.
• MSF urges international guarantors to enforce regular, uninterrupted fuel deliveries to all verified medical institutions.

'We are rationing kilowatts to keep incubators alive,' said Dr. Sarah Henderson, MSF Emergency Medical Lead in Khan Younis.""",
                source = "MSF / MIDDLE EAST EYE",
                url = "https://www.msf.org",
                imageUrl = "https://images.unsplash.com/photo-1513224502586-d1e602410265?auto=format&fit=crop&q=80&w=800",
                publishedAt = "24.08.2026 // 14:00 GMT",
                timestamp = now - (18 * hour),
                category = "HUMANITARIAN",
                isPalestine = true,
                isBreaking = false,
                isLive = false,
                location = "KHAN YOUNIS // SOUTH GAZA",
                keyTakeaways = "Generators rationed to life-support only, 400 trauma surgery patients waiting, Sterilization units facing outage",
                unReportReference = "WHO Health Cluster Bulletin #89"
            ),
            NewsArticle(
                id = "world_003",
                title = "European Union Foreign Affairs Council Votes on Human Rights Clause Compliance",
                summary = "EU ministers debate regulatory oversight on trade agreements and arms export licensing tied to compliance with international humanitarian standards.",
                fullContent = """BRUSSELS — The European Union Foreign Affairs Council convened in Brussels to review member states' compliance obligations under Article 2 of the EU-Israel Association Agreement, which stipulates respect for human rights and democratic principles.

Key developments from the debate:
• Spain, Ireland, and Belgium called for an official European Commission assessment of whether trade terms align with human rights benchmarks.
• Several member states reaffirmed export control directives preventing dual-use transfers where risks of international humanitarian law violations exist.
• High Representative for Foreign Affairs Josep Borrell emphasized the EU's unwavering commitment to rules-based international multilateralism.""",
                source = "EU COUNCIL / DEUTSCHE WELLE",
                url = "https://www.consilium.europa.eu",
                imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=800",
                publishedAt = "24.08.2026 // 11:20 GMT",
                timestamp = now - (21 * hour),
                category = "WORLD",
                isPalestine = false,
                isBreaking = false,
                isLive = false,
                location = "BRUSSELS // EU HQ",
                keyTakeaways = "Article 2 human rights compliance review, Arms export controls under scrutinization, Borrell affirms multilateral rule of law",
                unReportReference = "EU Council Briefing Doc 774/26"
            ),
            NewsArticle(
                id = "pal_006",
                title = "Al-Quds / East Jerusalem: UN Special Rapporteur Expresses Alarm Over Eviction Notices in Sheikh Jarrah and Silwan",
                summary = "Legal analysis points to violations of customary international law regarding property rights in occupied territory as dozens of families face displacement.",
                fullContent = """JERUSALEM — The UN Special Rapporteur on the situation of human rights in the Palestinian Territory, Francesca Albanese, issued a legal briefing addressing recent court orders enforcing eviction notices against Palestinian families in the Sheikh Jarrah and Batn al-Hawa (Silwan) neighborhoods of East Jerusalem.

The brief emphasizes:
1. Under Article 49 of the Fourth Geneva Convention, forced population transfers and expropriations in occupied territory are strictly prohibited.
2. Long-resident families who have inhabited homes under post-1948 Jordan-UN leaseholds face eviction based on discriminatory property restitution laws that do not grant reciprocal rights to Palestinians.
3. The UN Human Rights Office called on diplomatic missions in Jerusalem to monitor ongoing municipal court hearings.""",
                source = "UN OHCHR / WAFA",
                url = "https://www.ohchr.org",
                imageUrl = "https://images.unsplash.com/photo-1516216628859-9bccecab13ca?auto=format&fit=crop&q=80&w=800",
                publishedAt = "23.08.2026 // 16:45 GMT",
                timestamp = now - (40 * hour),
                category = "ANALYSIS",
                isPalestine = true,
                isBreaking = false,
                isLive = false,
                location = "EAST JERUSALEM",
                keyTakeaways = "Article 49 of 4th Geneva Convention cited, Sheikh Jarrah and Silwan families facing orders, UN OHCHR calls for diplomatic monitoring",
                unReportReference = "OHCHR Special Procedure Statement #14"
            ),
            NewsArticle(
                id = "world_004",
                title = "Global Climate Conference: Island Nations and MENA Region Seek Loss and Damage Implementation",
                summary = "Developing nations demand fast-track disbursement of climate adaptation funds as record heat waves impact agriculture and water security across the Mediterranean.",
                fullContent = """CAIRO / NAIROBI — Delegates from 80 climate-vulnerable countries gathered in Cairo for preparatory talks on climate adaptation financing.

Key focus areas:
• The Eastern Mediterranean and Middle East are warming at twice the global average, threatening aquifer recharge and wheat harvests.
• Small Island Developing States (SIDS) and Arab states presented joint proposals for unconditional emergency climate liquidity.
• The World Bank announced a $400 million concessional facility for wastewater reclamation in water-scarce regions.""",
                source = "UNEP / REUTERS",
                url = "https://www.unep.org",
                imageUrl = "https://images.unsplash.com/photo-1611273426858-450d8e3c9cce?auto=format&fit=crop&q=80&w=800",
                publishedAt = "23.08.2026 // 12:00 GMT",
                timestamp = now - (44 * hour),
                category = "WORLD",
                isPalestine = false,
                isBreaking = false,
                isLive = false,
                location = "CAIRO // EGYPT",
                keyTakeaways = "MENA warming at 2x global average, Water security crisis highlighted, $400M wastewater reclamation facility approved",
                unReportReference = "UNEP Climate Resilience Memo 2026"
            )
        )
    }
}
