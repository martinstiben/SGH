import React from 'react';
import { ScrollView, Image, View, Text, TouchableOpacity, Dimensions } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { styles } from '../styles/landingStyles';
import Header from '../components/Genericos/Header';
import InfoCard from '../components/Genericos/InfoCard';
import StatCard from '../components/Genericos/StatCard';
import { RootStackParamList } from '../navigation/types';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'Landing'>;

export default function LandingScreen() {
  const navigation = useNavigation<NavigationProp>();

  const handleLogin = () => {
    navigation.navigate('Login');
  };

  return (
    <ScrollView style={styles.mainContainer} showsVerticalScrollIndicator={false}>
      {/* Header principal */}
      <Header
        title="SGH"
        buttonLabel="Ingresar"
        onPress={handleLogin}
      />

      {/* Sección Hero mejorada */}
      <View style={styles.heroSection}>
        <Image source={require('../assets/images/logo.png')} style={styles.heroLogo} />
        <Text style={styles.heroTitle}>Sistema de Gestión de Horarios</Text>
        <Text style={styles.heroSubtitle}>
          La plataforma más completa y avanzada para la gestión inteligente de horarios académicos
        </Text>
      </View>


      {/* Estadísticas destacadas con mejor presentación */}
      <View style={styles.statsSection}>
        <Text style={styles.sectionTitle}>Resultados que hablan por sí solos</Text>
        <View style={styles.statsContainer}>
          <StatCard
            number="100%"
            label="Automatización completa en creación de horarios"
            icon={require('../assets/images/trophy.png')}
          />
          <StatCard
            number="0"
            label="Conflictos de horarios garantizados"
            icon={require('../assets/images/rocket.png')}
          />
        </View>
        <View style={styles.statsContainer}>
          <StatCard
            number="24/7"
            label="Disponibilidad continua del sistema"
            icon={require('../assets/images/shapes.png')}
          />
          <StatCard
            number="∞"
            label="Horarios completamente personalizables"
            icon={require('../assets/images/user.png')}
          />
        </View>
      </View>

      {/* Sección de beneficios con mejor diseño */}
      <View style={styles.benefitsSection}>
        <Text style={styles.sectionTitle}>Beneficios tangibles para tu institución</Text>
        
        <View style={styles.benefitCard}>
          <Text style={styles.benefitTitle}>⏰ Ahorro masivo de tiempo</Text>
          <Text style={styles.benefitDescription}>
            Reduce el tiempo de creación de horarios de días completos a solo minutos.
            Libera tiempo valioso para actividades más estratégicas.
          </Text>
        </View>

        <View style={styles.benefitCard}>
          <Text style={styles.benefitTitle}>🎯 Precisión absoluta garantizada</Text>
          <Text style={styles.benefitDescription}>
            Elimina completamente conflictos, errores humanos y inconsistencias.
            Cada horario generado es matemáticamente perfecto y viable.
          </Text>
        </View>

        <View style={styles.benefitCard}>
          <Text style={styles.benefitTitle}>📊 Análisis inteligente continuo</Text>
          <Text style={styles.benefitDescription}>
            Monitoreo y optimización automática de la carga académica.
            Informes detallados para mejorar continuamente la gestión educativa.
          </Text>
        </View>

        <View style={styles.benefitCard}>
          <Text style={styles.benefitTitle}>💡 Decisiones basadas en datos</Text>
          <Text style={styles.benefitDescription}>
            Estadísticas avanzadas y reportes detallados que facilitan la toma de
            decisiones estratégicas y mejoran la planificación académica.
          </Text>
        </View>
      </View>

      {/* Call to Action mejorado */}
      <View style={styles.ctaSection}>
        <Text style={styles.sectionTitle}>¿Listo para transformar tu gestión de horarios?</Text>
        <TouchableOpacity style={styles.ctaButton} onPress={handleLogin}>
          <Text style={styles.ctaButtonText}>Comenzar ahora</Text>
        </TouchableOpacity>
        <Text style={styles.ctaDescription}>
          Únete a cientos de instituciones que ya han revolucionado su gestión académica
        </Text>
      </View>

      {/* Footer profesional */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>
          © 2025 SGH - Sistema Inteligente de Gestión de Horarios{'\n'}
        </Text>
      </View>
    </ScrollView>
  );
}
